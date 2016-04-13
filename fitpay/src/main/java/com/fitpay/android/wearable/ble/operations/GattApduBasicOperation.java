package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.message.ApduControlWriteMessage;

/**
 * Created by Vlad on 05.04.2016.
 */
public class GattApduBasicOperation extends GattApduBaseOperation {

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
