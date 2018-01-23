package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.api.enums.SyncInitiator;
import com.fitpay.android.paymentdevice.models.SyncInfo;
import com.fitpay.android.utils.Constants;

/**
 * Request to sync from push notification.
 */
public class NotificationSyncRequest {
    private final SyncInfo syncInfo;

    public NotificationSyncRequest(String syncData) {
        this.syncInfo = Constants.getGson().fromJson(syncData, SyncInfo.class);
        this.syncInfo.setInitiator(SyncInitiator.PLATFORM);
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
