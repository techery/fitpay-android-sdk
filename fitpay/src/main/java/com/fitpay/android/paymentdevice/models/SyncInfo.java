package com.fitpay.android.paymentdevice.models;

import com.fitpay.android.api.enums.SyncInitiator;
import com.fitpay.android.api.models.sync.SyncLinks;

/**
 * Sync notification model (receiving it from push notification or webhook)
 */
public final class SyncInfo {
    private String id;
    private String deviceId;
    private String userId;
    private String clientId;
    private String type;
    private SyncLinks syncLinks;
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

    public SyncLinks getSyncLinks() {
        return syncLinks;
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
}
