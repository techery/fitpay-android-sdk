package com.fitpay.android.wearable.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.bluetooth.gatt.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.constants.DeviceInformationConstants;
import com.fitpay.android.wearable.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.utils.Hex;
import com.fitpay.android.wearable.utils.OperationQueue;

import java.util.UUID;

public class GattDeviceCharacteristicsOperation extends GattOperation {

    private BluetoothDevice mDevice;

    private String manufacturerName;
    private String modelNumber;
    private String serialNumber;
    private String hardwareRevision;
    private String firmwareRevision;
    private String softwareRevision;
    private String systemId;
    private String secureElementId;

    public GattDeviceCharacteristicsOperation(final BluetoothDevice device) {

        mDevice = device;

        OperationQueue bundle = new OperationQueue();

        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_MANUFACTURER_NAME_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        manufacturerName = Hex.bytesToHexString(characteristic);
                    }
                }
        ));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        modelNumber = Hex.bytesToHexString(characteristic);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        serialNumber = Hex.bytesToHexString(characteristic);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        firmwareRevision = Hex.bytesToHexString(characteristic);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        softwareRevision = Hex.bytesToHexString(characteristic);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        hardwareRevision = Hex.bytesToHexString(characteristic);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        systemId = Hex.bytesToHexString(characteristic);
                    }
                }));
        bundle.push(new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                UUID.fromString(PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID),
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] characteristic) {
                        secureElementId = Hex.bytesToHexString(characteristic);
                    }
                }));

        setNestedQueue(bundle);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Device device = new Device.Builder()
                .setBdAddress(mDevice.getAddress())
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

    private GattOperation createOperation(String characteristicUUID, GattCharacteristicReadCallback callback) {
        return new GattCharacteristicReadOperation(DeviceInformationConstants.SERVICE_UUID, UUID.fromString(characteristicUUID), callback);
    }
}
