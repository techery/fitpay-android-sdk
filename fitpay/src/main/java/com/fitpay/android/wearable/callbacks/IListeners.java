package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.Connection;
import com.fitpay.android.wearable.enums.Sync;

/**
 * Collection of wearable callbacks
 */
public final class IListeners {

    public interface ApduListener {
        void onApduPackageResultReceived(final ApduExecutionResult result);
        void onApduPackageErrorReceived(final ApduExecutionResult result);
    }

    public interface ConnectionListener {
        void onDeviceStateChanged(final @Connection.State int state);
    }

    public interface SyncListener {
        void onSyncStateChanged(final Sync syncEvent);
        void onNonApduCommit(final Commit commit);
    }

    public interface WearableListener extends ConnectionListener {
        void onDeviceInfoReceived(final Device device);
        void onNFCStateReceived(final boolean isEnabled, final byte errorCode);
        void onNotificationReceived(final byte[] data);
        void onApplicationControlReceived(final byte[] data);
    }
}
