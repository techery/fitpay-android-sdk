package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.paymentdevice.models.SyncInfo;

/**
 * Request to sync from push notification.
 */
public class NotificationSyncRequest {
    private final SyncInfo syncInfo;

    public NotificationSyncRequest(SyncInfo syncInfo) {
        this.syncInfo = syncInfo;
    }

    /**
     * Get sync info
     *
     * @return sync info
     */
    public SyncInfo getSyncInfo() {
        return syncInfo;
    }
}
