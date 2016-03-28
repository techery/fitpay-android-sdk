package com.fitpay.android.wearable;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.StringUtils;
import com.fitpay.android.wearable.bluetooth.gatt.GattManager;
import com.fitpay.android.wearable.bluetooth.gatt.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattCharacteristicReadOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattCharacteristicWriteOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattDeviceCharacteristicsOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattOperationBundle;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattSetIndicationOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattSetNotificationOperation;
import com.fitpay.android.wearable.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.models.NFCInfo;
import com.orhanobut.logger.Logger;

import java.util.UUID;

public class BluetoothService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private GattManager mGattManager;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The MAC address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {

        if (mBluetoothAdapter == null || StringUtils.isEmpty(address)) {
            Logger.w("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Logger.w("Device not found.  Unable to connect.");
            return false;
        }

        if(mDevice != device && mGattManager != null){
            mGattManager.close();
            mGattManager = null;
        }

        mDevice = device;
        mGattManager = new GattManager(this, device);

//        GattOperationBundle bundle = new GattOperationBundle();
//        bundle.addOperation(new GattDeviceCharacteristicsOperation(device));
//        mGattManager.queue(bundle);

        setNFCState(NFCInfo.STATE_ENABLED);
        getNFCState();
        return true;
    }

    public void getNFCState() {
        GattOperation getNFCOperation = new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE),
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        RxBus.getInstance().post(new NFCInfo(characteristic));
                    }
                });
        mGattManager.queue(getNFCOperation);
    }

    public void setNFCState(@NFCInfo.State int state){

        GattOperation setNFCIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE),
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mGattManager.queue(setNFCIndication);

        GattOperation setNFCOperation = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURITY_WRITE),
                NFCInfo.convertStateToByte(state)
        );
        mGattManager.queue(setNFCOperation);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean init() {
        Logger.d("initializing Gatt Server Service");

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager == null) {
            Logger.e("unable to initialize bluetooth manager");
            return false;
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.e("unable to obtain bluetooth adapter");
            return false;
        }

        return true;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mGattManager == null) {
            return;
        }

        mGattManager.close();
        mGattManager = null;
    }

//    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                if (mDeviceCallback != null) {
//                    mDeviceCallback.onConnect();
//                }
//
//                gatt.discoverServices();
//
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                if (mDeviceCallback != null) {
//                    mDeviceCallback.onDisconnect();
//                }
//            }
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
//
//                for (BluetoothGattService gattService : gattServices) {
//                    UUID uuid = gattService.getUuid();
//
//                    if(DeviceInformationConstants.SERVICE_UUID.equals(uuid)){
//                        for(BluetoothGattCharacteristic bgc : gattService.getCharacteristics()){
//                            mQueue.offer(bgc);
//                        }
//                    } else if(PaymentServiceConstants.SERVICE_UUID.equals(uuid)){
//                        for(BluetoothGattCharacteristic bgc : gattService.getCharacteristics()){
//                            mQueue.offer(bgc);
//                        }
//                    }
//                }
//
//                gatt.readCharacteristic(mQueue.poll());
//
//            } else {
//                Log.w(TAG, "onServicesDiscovered received: " + status);
//            }
//        }
//
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            if(status == BluetoothGatt.GATT_SUCCESS){
//
//                BluetoothGattCharacteristic nextRequest = mQueue.poll();
//                if(nextRequest != null){
//                     gatt.readCharacteristic(nextRequest);
//                }
//
//            }
//        }
//
//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//        }
//
//        @Override
//        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
////            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//            UUID cUUID = characteristic.getUuid();
//        }
//
//        @Override
//        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//        }
//
//        @Override
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//        }
//    };

}
