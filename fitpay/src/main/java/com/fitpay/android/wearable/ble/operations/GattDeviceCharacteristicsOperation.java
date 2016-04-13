package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.constants.DeviceInformationConstants;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.utils.Hex;

import java.util.UUID;

public class GattDeviceCharacteristicsOperation extends GattOperation {

    private String mAddress;

    private String manufacturerName;
    private String modelNumber;
    private String serialNumber;
    private String hardwareRevision;
    private String firmwareRevision;
    private String softwareRevision;
    private String systemId;
    private String secureElementId;

    public GattDeviceCharacteristicsOperation(final String macAddress) {

        mAddress = macAddress;

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_MANUFACTURER_NAME_STRING,
                data -> manufacturerName = Hex.bytesToHexString(data)));

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING,
                data -> modelNumber = Hex.bytesToHexString(data)));

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING,
                data -> serialNumber = Hex.bytesToHexString(data)));

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING,
                data -> firmwareRevision = Hex.bytesToHexString(data)));

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING,
                data -> softwareRevision = Hex.bytesToHexString(data)));

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING,
                data -> hardwareRevision = Hex.bytesToHexString(data)));

        addNestedOperation(createOperation(DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID,
                data -> systemId = Hex.bytesToHexString(data)));

        addNestedOperation(new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID,
                data -> secureElementId = Hex.bytesToHexString(data)));
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Device device = new Device.Builder()
                .setBdAddress(mAddress)
                .setModelNumber(modelNumber)
                .setManufacturerName(manufacturerName)
                .setSerialNumber(serialNumber)
                .setSystemId(systemId)
                .setSecureElementId(secureElementId)
                .setFirmwareRevision(firmwareRevision)
                .setSoftwareRevision(softwareRevision)
                .setHardwareRevision(hardwareRevision)
                .create();

        RxBus.getInstance().post(device);
    }

    private GattOperation createOperation(UUID characteristicUUID, GattCharacteristicReadCallback callback) {
        return new GattCharacteristicReadOperation(DeviceInformationConstants.SERVICE_UUID, characteristicUUID, callback);
    }
}
