package com.fitpay.android.paymentdevice.ble;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.apdu.ApduCommand;

/**
 * Apdu operation for commands with size < 17 bytes
 */
class GattApduBasicOperation extends GattApduBaseOperation {

    public GattApduBasicOperation(ApduCommand command) {
        super(command.getSequence());

        final ApduControlWriteMessage apduControlWriteMessage = new ApduControlWriteMessage()
                .withSequenceId(command.getSequence())
                .withData(command.getCommand());

        GattOperation apduControlWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL,
                apduControlWriteMessage.getMessage());

        addNestedOperation(apduControlWrite);
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        //wait for ApduResultMessage
    }
}
