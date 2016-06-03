package com.fitpay.android.paymentdevice.callbacks;

import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.utils.Listener;

/**
 * Synchronization callbacks
 */
public abstract class SyncListener extends Listener implements IListeners.SyncListener {
    public SyncListener() {
        super();
        mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
    }
}
