package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.wearable.enums.Sync;

/**
 * Created by Vlad on 05.04.2016.
 */
public abstract class SyncListener extends Listener implements IListeners.SyncListener {
    public SyncListener() {
        super();
        mCommands.put(Commit.class, data -> onNonApduCommit((Commit) data));
        mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
    }
}
