package com.fitpay.android.wearable.ble.operations;

import com.fitpay.android.wearable.ble.utils.OperationQueue;

/**
 * Created by Vlad on 05.04.2016.
 */
public abstract class GattApduBaseOperation extends GattOperation{

    private int sequenceId;

    public GattApduBaseOperation(int sequenceId){
        this.sequenceId = sequenceId;
        mNestedQueue = new OperationQueue();
    }

    public int getSequenceId(){
        return sequenceId;
    }
}
