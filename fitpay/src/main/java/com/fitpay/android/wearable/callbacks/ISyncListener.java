package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.wearable.enums.Sync;

/**
 * Created by Vlad on 11.04.2016.
 */
interface ISyncListener extends IApduListener{
    void onSyncStateChanged(@Sync.State int state);
    void onNonApduCommit(Commit commit);
}
