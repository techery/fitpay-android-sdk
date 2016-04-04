package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.StringUtils;
import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.message.SecurityStateMessage;
import com.fitpay.android.wearable.ble.operations.GattApduOperation;
import com.fitpay.android.wearable.ble.operations.GattCharacteristicReadOperation;
import com.fitpay.android.wearable.ble.operations.GattCharacteristicWriteOperation;
import com.fitpay.android.wearable.ble.operations.GattConnectOperation;
import com.fitpay.android.wearable.ble.operations.GattDeviceCharacteristicsOperation;
import com.fitpay.android.wearable.ble.operations.GattOperation;
import com.fitpay.android.wearable.ble.operations.GattOperationBundle;
import com.fitpay.android.wearable.ble.operations.GattSetIndicationOperation;
import com.fitpay.android.wearable.enums.States;
import com.fitpay.android.wearable.model.Wearable;
import com.orhanobut.logger.Logger;

/**
 * Manage data exchange with device via Bluetooth
 */
public final class BluetoothWearable extends Wearable {

    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private GattManager mGattManager;

    public BluetoothWearable(Context context, BluetoothDevice device) {
        super(context, device.getAddress());

        mDevice = device;

        BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager == null) {
            Logger.e("unable to initialize bluetooth manager");
            return;
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.e("unable to obtain bluetooth adapter");
            return;
        }

        initialized = true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @Override
    public void connect() {
        if (mBluetoothAdapter == null || StringUtils.isEmpty(mAddress)) {
            Logger.w("BluetoothAdapter not initialized or unspecified address.");
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
        if (device == null) {
            Logger.w("Device not found.  Unable to connect.");
            return;
        }

        mGattManager = new GattManager(mContext, device);
        mGattManager.queue(new GattConnectOperation());

        startDataIndication();
    }

    @Override
    public void disconnect() {
        mGattManager.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    @Override
    public void close() {
        mGattManager.close();
        mGattManager = null;
    }

    @Override
    public void getDeviceInfo() {
        GattOperationBundle bundle = new GattOperationBundle();
        bundle.addOperation(new GattDeviceCharacteristicsOperation(mDevice));
        mGattManager.queue(bundle);
    }

    @Override
    public void getNFCState() {
        GattOperation getNFCOperation = new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        RxBus.getInstance().post(new SecurityStateMessage().withData(data));
                    }
                });
        mGattManager.queue(getNFCOperation);
    }

    @Override
    public void setNFCState(@States.NFC byte state) {
        GattOperation setNFCOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE,
                new byte[]{state}
        );
        mGattManager.queue(setNFCOperation);
    }

    @Override
    public void sendApduPackage(ApduPackage apduPackage) {
        GattOperation sendApduOperation = new GattApduOperation(apduPackage);
        mGattManager.queue(sendApduOperation);
    }

    @Override
    public void sendTransactionData(byte[] data) {
        GattOperation setTransactionOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                data
        );
        mGattManager.queue(setTransactionOperation);
    }

    @Override
    public void setSecureElementState(@States.SecureElement byte state) {
        GattOperation resetOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_DEVICE_RESET,
                new byte[]{state}
        );
        mGattManager.queue(resetOperation);
    }

    private void startDataIndication() {

        GattOperationBundle bundle = new GattOperationBundle();

        GattOperation nfcIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(nfcIndication);

        GattOperation adpuResultIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(adpuResultIndication);

        GattOperation continuationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(continuationControlIndication);

        GattOperation continuationPacketIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(continuationPacketIndication);

        GattOperation transactionIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(transactionIndication);

        GattOperation applicationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        bundle.addOperation(applicationControlIndication);

        mGattManager.queue(bundle);
    }
}
