package com.fitpay.android.wearable.bluetooth.gatt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.StringUtils;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattConnectOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattOperation;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattOperationBundle;
import com.fitpay.android.wearable.bluetooth.gatt.operations.GattDeviceCharacteristicsReadOperator;
import com.fitpay.android.wearable.callbacks.DeviceCallback;
import com.orhanobut.logger.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BluetoothService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceCallback mDeviceCallback;

    private GattManager mGattManager;

    // Queues for characteristic read (synchronous)
    private ConcurrentLinkedQueue<BluetoothGattCharacteristic> mQueue;

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
        mQueue = new ConcurrentLinkedQueue<>();

        mGattManager = new GattManager(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setDeviceCallback(DeviceCallback callback) {
        mDeviceCallback = callback;
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

        if (mGattManager == null) {
            return false;
        }

        GattOperationBundle bundle = new GattOperationBundle();
//        bundle.addOperation(new GattConnectOperation(device));
        bundle.addOperation(new GattDeviceCharacteristicsReadOperator(device));
        mGattManager.queue(bundle);

        return true;
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
