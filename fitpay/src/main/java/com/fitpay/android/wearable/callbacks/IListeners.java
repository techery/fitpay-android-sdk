package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.Connection;
import com.fitpay.android.wearable.enums.Sync;

/**
 * Created by Vlad on 12.04.2016.
 */
public class IListeners {

    public interface ApduListener {
        void onApduPackageResultReceived(ApduExecutionResult result);
        void onApduPackageErrorReceived(ApduExecutionResult result);
    }

    public interface ConnectionListener {
        void onDeviceStateChanged(@Connection.State int state);
    }

    public interface SyncListener {
        void onSyncStateChanged(Sync syncEvent);
        void onNonApduCommit(Commit commit);
    }

    public interface WearableListener extends ConnectionListener {
        void onDeviceInfoReceived(Device device);
        void onNFCStateReceived(boolean isEnabled, byte errorCode);
        void onNotificationReceived(byte[] data);
        void onApplicationControlReceived(byte[] data);
    }
}
