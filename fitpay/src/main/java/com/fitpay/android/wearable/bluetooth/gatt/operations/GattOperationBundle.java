package com.fitpay.android.wearable.bluetooth.gatt.operations;

import java.util.LinkedList;
import java.util.Queue;

public class GattOperationBundle {
    final Queue<GattOperation> operations;

    public GattOperationBundle() {
        operations = new LinkedList<>();
    }

    public void addOperation(GattOperation operation) {
        operations.add(operation);
    }

    public Queue<GattOperation> getOperations() {
        return operations;
    }
}
