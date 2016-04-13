package com.fitpay.android.wearable.ble.operations;

/**
 * Created by Vlad on 05.04.2016.
 */
public abstract class GattApduBaseOperation extends GattOperation {

    private int sequenceId;

    public GattApduBaseOperation(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public int getSequenceId() {
        return sequenceId;
    }
}
