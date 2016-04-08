package com.fitpay.android.wearable.ble.utils;

import java.util.Iterator;
import java.util.LinkedList;

import com.fitpay.android.wearable.ble.operations.GattOperation;

/**
 * Created by Vlad on 26.03.2016.
 */
public class OperationQueue extends LinkedList<GattOperation> {

    public GattOperation getFirst() {
        GattOperation operation;

        Iterator iter = this.iterator();
        if (iter.hasNext()) {
            operation = (GattOperation) iter.next();

            if (!operation.hasNested()) {
                return poll();
            }

            OperationQueue bundle = operation.getNestedQueue();
            return bundle.getFirst();
        }

        return null;
    }
}
