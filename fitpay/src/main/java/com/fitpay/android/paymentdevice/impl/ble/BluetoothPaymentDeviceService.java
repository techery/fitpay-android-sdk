package com.fitpay.android.paymentdevice.impl.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.model.PaymentDeviceService;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.StringUtils;
import com.orhanobut.logger.Logger;

/**
 * BLE implementation
 */
public final class BluetoothPaymentDeviceService extends PaymentDeviceService {

    private final String TAG = BluetoothPaymentDeviceService.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private GattManager mGattManager;

    public BluetoothPaymentDeviceService(Context context, String deviceAddress) {
        super(context, deviceAddress);
      //  Logger.d(TAG, "create bt device");

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

        setState(States.INITIALIZED);
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
        Logger.d(TAG, "initiate connect to device: " + mAddress);
        if (mBluetoothAdapter == null || StringUtils.isEmpty(mAddress)) {
            Logger.w("BluetoothAdapter not initialized or unspecified address.");
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
        if (device == null) {
            Logger.w("Device not found.  Unable to connect.");
            return;
        }

        mGattManager = new GattManager(this, mContext, device);
        mGattManager.queue(new GattSubscribeOperation());

    }

    @Override
    public void disconnect() {
        Logger.d(TAG, "initiate disconnect from to device: " + mAddress);
        if(mGattManager != null) {
            mGattManager.disconnect();
        } else {
            Logger.w("GattManager is null");
        }
    }

    @Override
    public void reconnect() {
        if(mGattManager != null) {
            mGattManager.reconnect();
        } else {
            Logger.w("GattManager is null");
        }
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
    public void readDeviceInfo() {
        Log.d(TAG, "initiate readDeviceInfo request");
        GattOperation readDeviceInfoOperation = new GattDeviceCharacteristicsOperation(mAddress);
        mGattManager.queue(readDeviceInfoOperation);
    }

    @Override
    public void readNFCState() {
        Log.d(TAG, "initiate readNFCState request");
        GattOperation getNFCOperation = new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                data -> RxBus.getInstance().post(new SecurityStateMessage().withData(data)));
        mGattManager.queue(getNFCOperation);
    }

    @Override
    public void setNFCState(@NFC.Action byte state) {
        Log.d(TAG, "initiate setNFCState request.  Target state: " + state);
        GattOperation setNFCOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE,
                new byte[]{state}
        );
        mGattManager.queue(setNFCOperation);
    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {
        Log.d(TAG, "initiate executeApduPackage request");
        GattOperation sendApduOperation = new GattApduOperation(apduPackage);
        mGattManager.queue(sendApduOperation);
    }

    @Override
    public void sendNotification(byte[] data) {
        Log.d(TAG, "initiate sendNotification request.  data: " + data);
        GattOperation setTransactionOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                data
        );
        mGattManager.queue(setTransactionOperation);
    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {
        Log.d(TAG, "initiate setSecureElementState request.  Target state: " + state);
        GattOperation resetOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_DEVICE_RESET,
                new byte[]{state}
        );
        mGattManager.queue(resetOperation);
    }
}
