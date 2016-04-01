package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.message.ApduControlWriteMessage;
import com.fitpay.android.wearable.ble.message.ContinuationControlBeginMessage;
import com.fitpay.android.wearable.ble.message.ContinuationControlEndMessage;
import com.fitpay.android.wearable.ble.message.ContinuationPacketMessage;
import com.fitpay.android.wearable.ble.utils.OperationQueue;

public class GattApduOperation extends GattOperation {

    public GattApduOperation(ApduPackage apduPackage) {

        mNestedQueue = new OperationQueue();

        for (ApduCommand command : apduPackage.getApduCommands()) {
            if(command.getCommand().getBytes().length <= 17){
                createSimpleOperation(command);
            } else {
                createContinuationOperation(command);
            }
        }
    }

    @Override
    public void execute(BluetoothGatt gatt) {
    }

    private void createSimpleOperation(ApduCommand command){
        final ApduControlWriteMessage apduControlWriteMessage = new ApduControlWriteMessage()
                .withSequenceId(command.getSequence())
                .withData(command.getCommand().getBytes());

        GattOperation apduControlWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL,
                apduControlWriteMessage.getMessage());

        mNestedQueue.add(apduControlWrite);
    }

    private void createContinuationOperation(ApduCommand command){

        /*begin*/
        ContinuationControlBeginMessage beingMsg = new ContinuationControlBeginMessage()
                .withUuid(PaymentServiceConstants.CHARACTERISTIC_APDU_CONTROL);

        GattOperation continuationStartWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                beingMsg.getMessage());

        mNestedQueue.add(continuationStartWrite);

        /*packets*/
        int currentPos = 0;
        int sortOrder = 0;

        byte[] msg = command.getCommand().getBytes();

        byte[] dataToSend = null;
        while (currentPos < msg.length) {
            int len = Math.min(msg.length - currentPos, ContinuationPacketMessage.getMaxDataLength());
            dataToSend = new byte[len];
            System.arraycopy(msg, currentPos, dataToSend, 0, len);

            ContinuationPacketMessage packetMessage = new ContinuationPacketMessage()
                    .withSortOrder(sortOrder)
                    .withData(dataToSend);

            GattOperation packetWrite = new GattCharacteristicWriteOperation(
                    PaymentServiceConstants.SERVICE_UUID,
                    PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET,
                    packetMessage.getMessage());

            mNestedQueue.add(packetWrite);

            currentPos+=len;
            sortOrder++;
        }

        /*end*/
        ContinuationControlEndMessage endMsg= new ContinuationControlEndMessage()
                .withPayload(command.getCommand().getBytes());

        GattOperation continuationEndWrite = new GattCharacteristicWriteOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                endMsg.getMessage());

        mNestedQueue.add(continuationEndWrite);
    }
}
