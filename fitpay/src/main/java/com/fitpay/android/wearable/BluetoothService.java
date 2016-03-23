package com.fitpay.android.wearable;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.fitpay.android.wearable.callbacks.DeviceCallback;
import com.fitpay.android.wearable.services.ServiceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothService extends Service {
    private final String TAG = this.getClass().getName();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService mBluetoothGattService;

    private String mBluetoothDeviceAddress;

    private List<ServiceHandler> mServiceHandlers;

    private DeviceCallback mDeviceCallback;

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
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

    public void setDeviceCallback(DeviceCallback callback) {
        mDeviceCallback = callback;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {

        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
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

        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean init() {
        Log.d(TAG, "initializing Gatt Server Service");

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager == null) {
            Log.e(TAG, "unable to initialize bluetooth manager");
            return false;
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "unable to obtain bluetooth adapter");
            return false;
        }

        return true;
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
     * @param enabled        If true, enable notification.  False otherwise.
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


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (mDeviceCallback != null) {
                    mDeviceCallback.onConnect();
                }

                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mDeviceCallback != null) {
                    mDeviceCallback.onDisconnect();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

                for (BluetoothGattService gattService : gattServices) {
                    mBluetoothGattService = gattService;
                }
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
//
//    // Filtering by custom UUID is broken in Android 4.3 and 4.4, see:
//    //   http://stackoverflow.com/questions/18019161/startlescan-with-128-bit-uuids-doesnt-work-on-native-android-ble-implementation?noredirect=1#comment27879874_18019161
//    // This is a workaround function from the SO thread to manually parse advertisement data.
//    private List<UUID> parseUUIDs(final byte[] advertisedData) {
//        List<UUID> uuids = new ArrayList<UUID>();
//
//        int offset = 0;
//        while (offset < (advertisedData.length - 2)) {
//            int len = advertisedData[offset++];
//            if (len == 0)
//                break;
//
//            int type = advertisedData[offset++];
//            switch (type) {
//                case 0x02: // Partial list of 16-bit UUIDs
//                case 0x03: // Complete list of 16-bit UUIDs
//                    while (len > 1) {
//                        int uuid16 = advertisedData[offset++];
//                        uuid16 += (advertisedData[offset++] << 8);
//                        len -= 2;
//                        uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
//                    }
//                    break;
//                case 0x06:// Partial list of 128-bit UUIDs
//                case 0x07:// Complete list of 128-bit UUIDs
//                    // Loop through the advertised 128-bit UUID's.
//                    while (len >= 16) {
//                        try {
//                            // Wrap the advertised bits and order them.
//                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData, offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
//                            long mostSignificantBit = buffer.getLong();
//                            long leastSignificantBit = buffer.getLong();
//                            uuids.add(new UUID(leastSignificantBit,
//                                    mostSignificantBit));
//                        } catch (IndexOutOfBoundsException e) {
//                            // Defensive programming.
//                            //Log.e(LOG_TAG, e.toString());
//                            continue;
//                        } finally {
//                            // Move the offset to read the next uuid.
//                            offset += 15;
//                            len -= 16;
//                        }
//                    }
//                    break;
//                default:
//                    offset += (len - 1);
//                    break;
//            }
//        }
//        return uuids;
//    }
//
//    public static boolean hasMyService(byte[] scanRecord) {
//
//        // UUID we want to filter by (without hyphens)
//        final String myServiceID = "0000000000001000800000805F9B34FB";
//
//        // The offset in the scan record. In my case the offset was 13; it will probably be different for you
//        final int serviceOffset = 13;
//
//        try {
//
//            // Get a 16-byte array of what may or may not be the service we're filtering for
//            byte[] service = subarray(scanRecord, serviceOffset, serviceOffset + 16);
//
//            // The bytes are probably in reverse order, so we need to fix that
//            reverse(service);
//
//            // Get the hex string
//            String discoveredServiceID = Hex.bytesToHexString(service);
//
//            // Compare against our service
//            return myServiceID.equals(discoveredServiceID);
//
//        } catch (Exception e) {
//            return false;
//        }
//
//    }
//
//    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
//
//    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
//        if (array == null) {
//            return null;
//        }
//        if (startIndexInclusive < 0) {
//            startIndexInclusive = 0;
//        }
//        if (endIndexExclusive > array.length) {
//            endIndexExclusive = array.length;
//        }
//        int newSize = endIndexExclusive - startIndexInclusive;
//        if (newSize <= 0) {
//            return EMPTY_BYTE_ARRAY;
//        }
//
//        byte[] subarray = new byte[newSize];
//        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
//        return subarray;
//    }
//
//    public static void reverse(byte[] array) {
//        if (array == null) {
//            return;
//        }
//        int i = 0;
//        int j = array.length - 1;
//        byte tmp;
//        while (j > i) {
//            tmp = array[j];
//            array[j] = array[i];
//            array[i] = tmp;
//            j--;
//            i++;
//        }
//    }

}
