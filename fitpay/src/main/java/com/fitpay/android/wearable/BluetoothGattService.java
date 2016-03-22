package com.fitpay.android.wearable;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothGattService extends Service{
    private final String TAG = this.getClass().getName();

    private String mBluetoothDeviceAddress;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mLEScanner;
    private List<ServiceHandler> mServiceHandlers;

    private Handler mHandler;

    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private static final long SCAN_PERIOD = 10000;

    private ScanSettings settings;
    private List<ScanFilter> filters;

    public class LocalBinder extends Binder {
        BluetoothGattService getService() {
            return BluetoothGattService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

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
        mServiceHandlers = new ArrayList<>();
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mServiceHandlers != null) {
            for (ServiceHandler handler : mServiceHandlers) {
                handler.close();
            }

            mServiceHandlers.clear();
            mServiceHandlers = null;
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }


    @SuppressLint("NewApi")
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
            scanLeDevice(false);// will stop after first device detection

            Log.d(TAG, "Trying to create a new connection.");
            mBluetoothDeviceAddress = device.getAddress();
            mConnectionState = STATE_CONNECTING;
        }
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean init() {
        Log.d(TAG, "initializing Gatt Server Service");

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (mBluetoothManager == null) {
                Log.e(TAG, "unable to initialize bluetooth manager");
                return false;
            }
        }


        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "unable to obtain bluetooth adapter");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        connectToDevice(device);
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        // This is specific to Heart Rate Measurement.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            UUID cUUID = characteristic.getUuid();
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }
    };

    public ServiceHandler getServiceHandler(Class clazz) {
        if (mServiceHandlers != null) {
            for (ServiceHandler candidate : mServiceHandlers) {
                if (candidate.getClass().equals(clazz)) {
                    return candidate;
                }
            }
        }

        return null;
    }
}
