package com.fitpay.android.wearable.services.deviceinfo;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.fitpay.android.wearable.services.ServiceHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ssteveli on 1/23/16.
 */
public class DeviceInformationServiceHandler extends ServiceHandler {

    private final String LOG_TAG = this.getClass().getName();

    private final static String DEVICE_SERIAL_NUMBER = UUID.randomUUID().toString();

    private final static String[] READABLE_CHARACTERISTICS = {
            DeviceInformationConstants.CHARACTERISTIC_MANUFACTURER_NAME_STRING,
            DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING,
            DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING,
            DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING,
            DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING,
            DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING,
            DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID
    };


    private BluetoothGattService deviceInfoService;
    private Map<String, String> characteristicNames = new HashMap<>();

    private DeviceInfoProvider deviceInfoProvider;

    public DeviceInformationServiceHandler(Context context) {
        super(context);
    }




    @Override
    public UUID getServiceUUID() {
        return DeviceInformationConstants.SERVICE_UUID;
    }



    @Override
    public boolean canHandleCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        String candidateId = characteristic.getUuid().toString().toLowerCase();

       for (String uuid: READABLE_CHARACTERISTICS) {
           if (candidateId.equals(uuid)) {
               return true;
           }
       }

        return false;
    }

    @Override
    public void handleCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_MANUFACTURER_NAME_STRING))) {
            characteristic.setValue(deviceInfoProvider.getManufacturerName());
        } else if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING))) {
            characteristic.setValue(deviceInfoProvider.getModelNumber());
        } else if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING))) {
            characteristic.setValue(deviceInfoProvider.getSerialNumber());
        } else if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING))) {
            characteristic.setValue(deviceInfoProvider.getSoftwareRevisionNumber());
        } else if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING))) {
            characteristic.setValue(deviceInfoProvider.getHardwareRevisionNumber());
        } else if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING))) {
            characteristic.setValue(deviceInfoProvider.getFirmwareRevisionNumber());
        } else if (characteristic.getUuid().equals(UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID))) {
            characteristic.setValue(deviceInfoProvider.getSystemId());
        }
    }

    @Override
    public BluetoothGattService buildService() {
        Log.d(LOG_TAG, "Building Device Info Service");
        deviceInfoService =
                new BluetoothGattService(DeviceInformationConstants.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_MANUFACTURER_NAME_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_MANUFACTURER_NAME_STRING, "Manufacturer Name");

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING, "Model Number");

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING, "Serial Number");

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING, "Firmware Number");

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING, "Hardware Revision");

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING, "Software Revision");

        characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        deviceInfoService.addCharacteristic(characteristic);
        characteristicNames.put(DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID, "System ID");

        return deviceInfoService;
    }


    @Override
    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return deviceInfoService.getCharacteristics();
    }

    @Override
    public String getCharacteristicName(BluetoothGattCharacteristic characteristic) {
        return characteristicNames.get(characteristic.getUuid().toString());
    }

    public DeviceInfoProvider getDeviceInfoProvider() {
        return deviceInfoProvider;
    }

    public void setDeviceInfoProvider(DeviceInfoProvider deviceInfoProvider) {
        this.deviceInfoProvider = deviceInfoProvider;
    }
}
