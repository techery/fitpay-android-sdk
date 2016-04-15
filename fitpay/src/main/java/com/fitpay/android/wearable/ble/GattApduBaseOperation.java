package com.fitpay.android.wearable.ble;

/**
 * Base apdu operation
 */
abstract class GattApduBaseOperation extends GattOperation {

    private int sequenceId;

    public GattApduBaseOperation(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public int getSequenceId() {
        return sequenceId;
    }
}
