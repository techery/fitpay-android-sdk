package com.fitpay.android.wearable.services.currenttime;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.fitpay.android.wearable.services.ServiceHandler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by ssteveli on 1/22/16.
 */
public class CurrentTimeServiceHandler extends ServiceHandler {
    private final static String TAG = CurrentTimeServiceHandler.class.getCanonicalName();

    private Calendar currentTime;

    public CurrentTimeServiceHandler(Context mContext) {
        super(mContext);
    }

    @Override
    public BluetoothGattService buildService() {
        BluetoothGattService currentTimeService =
                new BluetoothGattService(CurrentTimeConstants.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        currentTimeService.addCharacteristic(new BluetoothGattCharacteristic(
                UUID.fromString(CurrentTimeConstants.CHARACTERISTIC_CURRENT_TIME),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ));

        return currentTimeService;
    }

    @Override
    public boolean canHandleCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        String candidateId = characteristic.getUuid().toString().toLowerCase();

        if (candidateId.equals(CurrentTimeConstants.CHARACTERISTIC_CURRENT_TIME) ||
                candidateId.equals(CurrentTimeConstants.CHARACTERISTIC_LOCAL_TIME_INFORMATION) ||
                candidateId.equals(CurrentTimeConstants.CHARACTERISTIC_REFERENCE_TIME_INFORMATION)) {
            return true;
        }

        return false;
    }

    @Override
    public void handleCharacteristicRead(BluetoothGattCharacteristic characteristic) {

        if (characteristic.getUuid().equals(UUID.fromString(CurrentTimeConstants.CHARACTERISTIC_CURRENT_TIME))) {
            Calendar c = Calendar.getInstance();
            characteristic.setValue(c.get(Calendar.YEAR), BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            characteristic.setValue(c.get(Calendar.MONTH), BluetoothGattCharacteristic.FORMAT_UINT8, 2);
            characteristic.setValue(c.get(Calendar.DAY_OF_MONTH), BluetoothGattCharacteristic.FORMAT_UINT8, 3);
            characteristic.setValue(c.get(Calendar.HOUR_OF_DAY), BluetoothGattCharacteristic.FORMAT_UINT8, 4);
            characteristic.setValue(c.get(Calendar.MINUTE), BluetoothGattCharacteristic.FORMAT_UINT8, 5);
            characteristic.setValue(c.get(Calendar.SECOND), BluetoothGattCharacteristic.FORMAT_UINT8, 6);
        }


    }

    @Override
    public UUID getServiceUUID() {
        return CurrentTimeConstants.SERVICE_UUID;
    }

    @Override
    public List<String> getCharacteristicsToSubscribe() {
        return Arrays.asList(
                CurrentTimeConstants.CHARACTERISTIC_CURRENT_TIME
        );
    }

    @Override
    public void close() {
        currentTime = null;
    }
}
