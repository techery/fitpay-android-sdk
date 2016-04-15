package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.StringUtils;
import com.fitpay.android.wearable.constants.States;
import com.fitpay.android.wearable.enums.NFC;
import com.fitpay.android.wearable.enums.SecureElement;
import com.fitpay.android.wearable.model.Wearable;
import com.orhanobut.logger.Logger;

/**
 * BLE implementation
 */
public final class BluetoothWearable extends Wearable {

    private BluetoothAdapter mBluetoothAdapter;
    private GattManager mGattManager;

    public BluetoothWearable(Context context, String deviceAddress) {
        super(context, deviceAddress);

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
        GattOperation readDeviceInfoOperation = new GattDeviceCharacteristicsOperation(mAddress);
        mGattManager.queue(readDeviceInfoOperation);
    }

    @Override
    public void readNFCState() {
        GattOperation getNFCOperation = new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                data -> RxBus.getInstance().post(new SecurityStateMessage().withData(data)));
        mGattManager.queue(getNFCOperation);
    }

    @Override
    public void setNFCState(@NFC.Action byte state) {
        GattOperation setNFCOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE,
                new byte[]{state}
        );
        mGattManager.queue(setNFCOperation);
    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {
        GattOperation sendApduOperation = new GattApduOperation(apduPackage);
        mGattManager.queue(sendApduOperation);
    }

    @Override
    public void sendNotification(byte[] data) {
        GattOperation setTransactionOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                data
        );
        mGattManager.queue(setTransactionOperation);
    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {
        GattOperation resetOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_DEVICE_RESET,
                new byte[]{state}
        );
        mGattManager.queue(resetOperation);
    }
}
