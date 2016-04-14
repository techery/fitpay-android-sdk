package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothGatt;

/**
 * Subscribe to different notifications from a payment device.
 */
class GattSubscribeOperation extends GattOperation {

    public GattSubscribeOperation() {

        GattOperation nfcIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_SECURITY_STATE,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        addNestedOperation(nfcIndication);

        GattOperation adpuResultIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APDU_RESULT,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        addNestedOperation(adpuResultIndication);

        GattOperation continuationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        addNestedOperation(continuationControlIndication);

        GattOperation continuationPacketIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_CONTINUATION_PACKET,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        addNestedOperation(continuationPacketIndication);

        GattOperation transactionIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_NOTIFICATION,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        addNestedOperation(transactionIndication);

        GattOperation applicationControlIndication = new GattSetIndicationOperation(
                PaymentServiceConstants.SERVICE_UUID,
                PaymentServiceConstants.CHARACTERISTIC_APPLICATION_CONTROL,
                PaymentServiceConstants.CLIENT_CHARACTERISTIC_CONFIG
        );
        addNestedOperation(applicationControlIndication);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        //do nothing here
    }

    @Override
    public boolean canRunNextOperation() {
        return true;
    }
}
