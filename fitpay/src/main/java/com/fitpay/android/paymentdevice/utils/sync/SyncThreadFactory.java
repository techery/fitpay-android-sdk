package com.fitpay.android.paymentdevice.utils.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fitpay.android.paymentdevice.DeviceSyncManager;
import com.fitpay.android.paymentdevice.models.SyncRequest;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Vlad on 24.07.2017.
 */

public class SyncThreadFactory implements ThreadFactory {

    private final Context mContext;
    private final List<DeviceSyncManager.DeviceSyncManagerCallback> syncManagerCallbacks;
    private final BlockingQueue<SyncRequest> requests;

    public SyncThreadFactory(Context mContext, List<DeviceSyncManager.DeviceSyncManagerCallback> syncManagerCallbacks, BlockingQueue<SyncRequest> requests) {
        this.mContext = mContext;
        this.syncManagerCallbacks = syncManagerCallbacks;
        this.requests = requests;
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        return new SyncWorkerThread(mContext, syncManagerCallbacks, requests);
    }
}
