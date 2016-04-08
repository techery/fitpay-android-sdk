package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.wearable.enums.SyncEvent;

/**
 * Created by Vlad on 05.04.2016.
 */
public interface SyncListener extends ApduListener{
    void onSyncStateChanged(@SyncEvent.State int state);
    void onNonApduCommit(Commit commit);
}
