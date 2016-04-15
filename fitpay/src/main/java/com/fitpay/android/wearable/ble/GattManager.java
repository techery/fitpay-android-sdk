package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.constants.States;
import com.fitpay.android.wearable.enums.ApduExecutionError;
import com.fitpay.android.wearable.interfaces.ISecureMessage;
import com.fitpay.android.wearable.interfaces.IWearable;
import com.fitpay.android.wearable.utils.Crc32;
import com.fitpay.android.wearable.utils.Hex;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Manager that works with Bluetooth GATT Profile.
 **/
final class GattManager {

    private IWearable mWearable;

    private Context mContext;
    private BluetoothGatt mGatt;
    private BluetoothDevice mDevice;

    private OperationQueue mQueue;
    private GattOperation mCurrentOperation = null;

    private ContinuationPayload mContinuationPayload = null;
    private int mLastApduSequenceId;

    private AsyncTask<Void, Void, Void> mCurrentOperationTimeout;

    public GattManager(IWearable wearable, Context context, BluetoothDevice device) {
        mWearable = wearable;
        mContext = context;
        mDevice = device;
        mQueue = new OperationQueue();
    }

    public void reconnect() {
        queue(new GattSubscribeOperation());
    }

    public synchronized void disconnect() {
        if (mCurrentOperationTimeout != null) {
            mCurrentOperationTimeout.cancel(true);
        }

        setCurrentOperation(null);

        mQueue.clear();

        mWearable.setState(States.DISCONNECTING);

        if (mGatt != null) {
            mGatt.disconnect();
        }
    }

    public synchronized void close() {
        mQueue.clear();

        if (mGatt != null) {
            mGatt.close();
            mGatt = null;
        }
    }

    public synchronized void cancelCurrentOperationBundle() {
        Logger.w("Cancelling current operation. Queue size before: " + mQueue.size());
        processError(ApduExecutionError.ON_TIMEOUT);
    }

    public synchronized void queue(GattOperation gattOperation) {
        mQueue.add(gattOperation);
        Logger.i("Queueing Gatt operation, size will now become: " + mQueue.size());
        drive();
    }

    private synchronized void drive() {
        if (mCurrentOperation != null) {
            Logger.e("tried to drive, but currentOperation was not null, " + mCurrentOperation);
            return;
        }

        if (mQueue.size() == 0) {
            Logger.i("Queue empty, drive loop stopped.");
            mCurrentOperation = null;
            if (mCurrentOperationTimeout != null) {
                mCurrentOperationTimeout.cancel(true);
            }
            return;
        }

        final GattOperation operation = mQueue.getFirst();
        setCurrentOperation(operation);

        resetTimer(operation.getTimeoutMs());

        if (operation instanceof GattApduBaseOperation) {
            mLastApduSequenceId = ((GattApduBaseOperation) operation).getSequenceId();
        }

        if (mGatt != null) {
            execute(mGatt, operation);
        } else {
            mWearable.setState(States.CONNECTING);

            mDevice.connectGatt(mContext, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    switch (newState) {
                        case BluetoothProfile.STATE_CONNECTED:
                            mWearable.setState(States.CONNECTED);

                            Logger.i("Gatt connected to device " + mDevice.getAddress());

                            mGatt = gatt;
                            mGatt.discoverServices();
                            break;

                        case BluetoothProfile.STATE_DISCONNECTED:
                            mWearable.setState(States.DISCONNECTED);

                            Logger.i("Disconnected from gatt server " + mDevice.getAddress() + ", newState: " + newState);

                            setCurrentOperation(null);

                            //Fix: Android Issue 97501:	BLE reconnect issue
                            if (mGatt != null) {
                                close();
                            } else {
                                mQueue.clear();
                                gatt.close();
                            }

                            break;

                        case BluetoothProfile.STATE_CONNECTING:
                            mWearable.setState(States.CONNECTING);
                            break;

                        case BluetoothProfile.STATE_DISCONNECTING:
                            mWearable.setState(States.DISCONNECTING);
                            break;
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);

                    Logger.d("services discovered, status: " + status);
                    execute(gatt, operation);
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);

                    if (mCurrentOperation instanceof DataReader) {
                        ((DataReader) mCurrentOperation).onRead(characteristic.getValue());
                    }

                    driveNext();
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);

                    Logger.d("Characteristic " + characteristic.getUuid() + "written to on device " + mDevice.getAddress());

                    driveNext();
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);

                    UUID uuid = characteristic.getUuid();
                    byte[] value = characteristic.getValue();

                    Logger.d("Characteristic changed: " + uuid);

                    if (PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE.equals(uuid)) {
                        ISecureMessage securityStateMessage = new SecurityStateMessage().withData(value);
                        RxBus.getInstance().post(securityStateMessage);
                    } else if (PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION.equals(uuid)) {
                        NotificationMessage notificationMessage = new NotificationMessage().withData(value);
                        RxBus.getInstance().post(notificationMessage);
                    } else if (PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT.equals(uuid)) {
                        ApduResultMessage apduResultMessage = new ApduResultMessage().withMessage(value);

                        if (mLastApduSequenceId == apduResultMessage.getSequenceId()) {

                            postMessage(apduResultMessage);

//                            RxBus.getInstance().post(apduResultMessage);
//                            driveNext();
                        } else {
                            Logger.e("Wrong sequenceID. lastSequenceID:" + mLastApduSequenceId + " currentID:" + apduResultMessage.getSequenceId());
                            processError(ApduExecutionError.WRONG_SEQUENCE);
                        }
                    } else if (PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL.equals(uuid)) {
                        Logger.d("continuation control write received [" + Hex.bytesToHexString(value) + "], length [" + value.length + "]");
                        ContinuationControlMessage continuationControlMessage = ContinuationControlMessageFactory.withMessage(value);
                        Logger.d("continuation control message: " + continuationControlMessage);

                        // start continuation packet
                        if (continuationControlMessage instanceof ContinuationControlBeginMessage) {
                            if (mContinuationPayload != null) {
                                Logger.d("continuation was previously started, resetting to blank");
                            }

                            mContinuationPayload = new ContinuationPayload(((ContinuationControlBeginMessage) continuationControlMessage).getUuid());

                            Logger.d("continuation start control received, ready to receive continuation data");
                        } else if (continuationControlMessage instanceof ContinuationControlEndMessage) {
                            Logger.d("continuation control end received.  process update to characteristic: " + mContinuationPayload.getTargetUuid());

                            UUID targetUuid = mContinuationPayload.getTargetUuid();
                            byte[] payloadValue = null;
                            try {
                                payloadValue = mContinuationPayload.getValue();
                                mContinuationPayload = null;
                                Logger.d("complete continuation data [" + Hex.bytesToHexString(payloadValue) + "]");
                            } catch (IOException e) {
                                Logger.e("error parsing continuation data", e);
                                processError(ApduExecutionError.CONTINUATION_ERROR);
                                return;
                            }

                            long checkSumValue = Crc32.getCRC32Checksum(payloadValue);
                            long expectedChecksumValue = ((ContinuationControlEndMessage) continuationControlMessage).getChecksum();

                            if (checkSumValue != expectedChecksumValue) {
                                Logger.e("Checksums not equal.  input data checksum: " + checkSumValue
                                        + ", expected value as provided on continuation end: " + expectedChecksumValue);

                                processError(ApduExecutionError.WRONG_CHECKSUM);
                                return;
                            }

                            if (PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT.equals(targetUuid)) {
                                Logger.d("continuation is for APDU Result");

                                ApduResultMessage apduResultMessage = new ApduResultMessage().withMessage(payloadValue);
//                                RxBus.getInstance().post(apduResultMessage);
//
//                                driveNext();

                                postMessage(apduResultMessage);

                            } else {
                                Logger.w("Code does not handle continuation for characteristic: " + targetUuid);
                                processError(ApduExecutionError.CONTINUATION_ERROR);
                            }
                        }
                    } else if (PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET.equals(uuid)) {

                        Logger.d("continuation data packet received [" + Hex.bytesToHexString(value) + "]");
                        ContinuationPacketMessage continuationPacketMessage = new ContinuationPacketMessage().withMessage(value);
                        Logger.d("parsed continuation packet message: " + continuationPacketMessage);

                        if (mContinuationPayload == null) {
                            Logger.e("invalid continuation, no start received on control characteristic");
                            processError(ApduExecutionError.CONTINUATION_ERROR);
                            return;
                        }

                        try {
                            mContinuationPayload.processPacket(continuationPacketMessage);
                        } catch (Exception e) {
                            Logger.e("exception handling continuation packet", e);
                            processError(ApduExecutionError.CONTINUATION_ERROR);
                        }

                    } else if (PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL.equals(uuid)) {
                        ApplicationControlMessage applicationControlMessage = new ApplicationControlMessage()
                                .withData(value);
                        RxBus.getInstance().post(applicationControlMessage);
                    }
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);

                    if (mCurrentOperation instanceof DataReader) {
                        ((GattDescriptorReadOperation) mCurrentOperation).onRead(descriptor.getValue());
                    }

                    driveNext();
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                    driveNext();
                }
            });
        }
    }

    private void driveNext() {
        setCurrentOperation(null);
        drive();
    }

    private void execute(BluetoothGatt gatt, GattOperation operation) {
        if (operation != mCurrentOperation) {
            return;
        }

        operation.execute(gatt);

        if (operation.canRunNextOperation()) {
            driveNext();
        }
    }

    public synchronized void setCurrentOperation(GattOperation currentOperation) {
        mCurrentOperation = currentOperation;
    }

    private void processError(@ApduExecutionError.Reason int reason) {

        GattOperation parent = null;

        if (mCurrentOperation != null) {
            parent = GattOperation.getRoot(mCurrentOperation);
            mQueue.remove(parent);
        }

        if (parent != null && parent instanceof GattApduOperation) {
            RxBus.getInstance().post(new ApduExecutionError(reason));
        } else {
            driveNext();
        }
    }

    private void resetTimer(final long timeout) {
        if (mCurrentOperationTimeout != null) {
            mCurrentOperationTimeout.cancel(true);
        }
        mCurrentOperationTimeout = new AsyncTask<Void, Void, Void>() {
            @Override
            protected synchronized Void doInBackground(Void... voids) {
                try {
                    Logger.i("Starting to do a background timeout");
                    wait(timeout);
                } catch (InterruptedException e) {
                    Logger.i("was interrupted out of the timeout");
                }
                if (isCancelled()) {
                    Logger.i("The timeout was cancelled, so we do nothing.");
                    return null;
                }
                Logger.i("Timeout ran to completion, time to cancel the entire operation bundle. Abort, abort!");
                cancelCurrentOperationBundle();
                return null;
            }

            @Override
            protected synchronized void onCancelled() {
                super.onCancelled();
                notify();
            }
        }.execute();
    }

    private void postMessage(final ApduResultMessage message) {
        RxBus.getInstance().post(message);

        Observable.create(
                subscriber -> {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {}, throwable -> {}, () -> {
                    driveNext();
                });
    }
}
