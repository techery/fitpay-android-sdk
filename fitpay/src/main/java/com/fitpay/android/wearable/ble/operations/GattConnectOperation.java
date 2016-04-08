package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.wearable.ble.constants.PaymentServiceConstants;
import com.fitpay.android.wearable.ble.utils.OperationQueue;

public class GattConnectOperation extends GattOperation {

    public GattConnectOperation() {

        mNestedQueue = new OperationQueue();

        GattOperation nfcIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mNestedQueue.add(nfcIndication);

        GattOperation adpuResultIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mNestedQueue.add(adpuResultIndication);

        GattOperation continuationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mNestedQueue.add(continuationControlIndication);

        GattOperation continuationPacketIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mNestedQueue.add(continuationPacketIndication);

        GattOperation transactionIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mNestedQueue.add(transactionIndication);

        GattOperation applicationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        mNestedQueue.add(applicationControlIndication);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        //do nothing here
    }

    @Override
    public boolean canRunNextOperation(){
        return true;
    }
}
