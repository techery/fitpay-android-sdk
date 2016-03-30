package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.constants.DeviceInformationConstants;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.utils.Hex;
import com.fitpay.android.wearable.ble.utils.OperationQueue;

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
                    public void call(byte[] data) {
                        manufacturerName = Hex.bytesToHexString(data);
                    }
                }
        ));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_MODEL_NUMBER_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        modelNumber = Hex.bytesToHexString(data);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_SERIAL_NUMBER_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        serialNumber = Hex.bytesToHexString(data);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_FIRMWARE_REVISION_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        firmwareRevision = Hex.bytesToHexString(data);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_SOFTWARE_REVISION_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        softwareRevision = Hex.bytesToHexString(data);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_HARDWARE_REVISION_STRING,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        hardwareRevision = Hex.bytesToHexString(data);
                    }
                }));
        bundle.push(createOperation(DeviceInformationConstants.CHARACTERISTIC_SYSTEM_ID,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        systemId = Hex.bytesToHexString(data);
                    }
                }));
        bundle.push(new GattCharacteristicReadOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURE_ELEMENT_ID,
                new GattCharacteristicReadCallback() {
                    @Override
                    public void call(byte[] data) {
                        secureElementId = Hex.bytesToHexString(data);
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

    private GattOperation createOperation(UUID characteristicUUID, GattCharacteristicReadCallback callback) {
        return new GattCharacteristicReadOperation(DeviceInformationConstants.SERVICE_UUID, characteristicUUID, callback);
    }
}
