package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.constants.DeviceInformationConstants;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.message.ApduControlWriteMessage;
import com.fitpay.android.wearable.ble.utils.OperationQueue;

import java.util.UUID;

public class GattApduOperation extends GattOperation {

    public GattApduOperation(int sequenceId, byte[] data) {

        OperationQueue bundle = new OperationQueue();

        final ApduControlWriteMessage apduControlWriteMessage = new ApduControlWriteMessage()
                .withSequenceId(sequenceId)
                .withData(data);

        GattOperation apduControlWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL,
                apduControlWriteMessage.getMessage());

        bundle.push(apduControlWrite);

        setNestedQueue(bundle);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
    }

    private GattOperation createOperation(UUID characteristicUUID, GattCharacteristicReadCallback callback) {
        return new GattCharacteristicReadOperation(DeviceInformationConstants.SERVICE_UUID, characteristicUUID, callback);
    }
}
