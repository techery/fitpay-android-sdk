package com.fitpay.android.paymentdevice.models;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.SyncInitiator;
import com.fitpay.android.api.models.BaseModel;
import com.fitpay.android.api.models.sync.SyncMetricsData;

/**
 * Sync notification model (receiving it from push notification or webhook)
 */
public final class SyncInfo extends BaseModel {

    private static final String ACK_SYNC = "ackSync";
    private static final String COMPLETE_SYNC = "completeSync";

    private String id;
    private String deviceId;
    private String userId;
    private String clientId;
    private String type;
    @SyncInitiator.Initiator
    private String initiator;

    private SyncInfo() {
    }

    public String getSyncId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getType() {
        return type;
    }

    public String getInitiator() {
        return initiator;
    }

    /**
     * Set sync initiator {@link SyncInitiator}
     *
     * @param initiator initiator
     */
    public void setInitiator(@SyncInitiator.Initiator String initiator) {
        this.initiator = initiator;
    }

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
        makeNoResponsePostCall(COMPLETE_SYNC, data, callback);
    }

}
