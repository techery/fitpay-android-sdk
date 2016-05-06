package com.fitpay.android.paymentdevice.callbacks;

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.paymentdevice.enums.Sync;

/**
 * Synchronization callbacks
 */
public abstract class SyncListener extends Listener implements IListeners.SyncListener {
    public SyncListener() {
        super();
        mCommands.put(Commit.class, data -> onNonApduCommit((Commit) data));
        mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
    }
}
