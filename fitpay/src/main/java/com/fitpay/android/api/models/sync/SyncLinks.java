package com.fitpay.android.api.models.sync;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.BaseModel;

/**
 * Sync links to identify the source of syncing issues.
 */
public class SyncLinks extends BaseModel {

    private static final String ACK_SYNC = "ackSync";
    private static final String COMMIT_METRICS = "commitMetrics";

    /**
     * Send ack sync data
     *
     * @param syncId sync id
     * @param callback result callback
     */
    public void sendAckSync(@NonNull String syncId, @NonNull ApiCallback<Void> callback) {
        makeNoResponsePostCall(ACK_SYNC, syncId, callback);
    }

    /**
     * Send metrics data
     *
     * @param data metrics data
     * @param callback result callback
     */
    public void sendSyncMetrics(@NonNull SyncMetricsData data, @NonNull ApiCallback<Void> callback) {
        makeNoResponsePostCall(COMMIT_METRICS, data, callback);
    }
}
