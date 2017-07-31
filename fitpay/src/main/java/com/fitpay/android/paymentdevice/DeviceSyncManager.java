package com.fitpay.android.paymentdevice;

import android.content.Context;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.paymentdevice.utils.sync.SyncThreadExecutor;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Device sync manager can work with one device only, all new sync request will be put in a queue
 */
public class DeviceSyncManager {
    private final Context mContext;

    private final BlockingQueue<Runnable> requests;
    private final List<DeviceSyncManagerCallback> syncManagerCallbacks = new CopyOnWriteArrayList<>();

    private int queueSize;
    private int threadsCount;

    private SyncThreadExecutor worker;

    public DeviceSyncManager(Context context) {
        this.mContext = context;
        queueSize = Integer.parseInt(ApiManager.getConfig().get(ApiManager.PROPERTY_SYNC_QUEUE_SIZE));
        threadsCount = Integer.parseInt(ApiManager.getConfig().get(ApiManager.PROPERTY_SYNC_THREADS_COUNT));
        requests = new ArrayBlockingQueue<>(queueSize);
    }

    /**
     * This interface is more about testing and allowing unit tests to inject hooks into the process making this
     * class more testable.
     */
    public interface DeviceSyncManagerCallback {
        void syncRequestAdded(SyncRequest request);

        void syncRequestFailed(SyncRequest request);

        void syncTaskStarting(SyncRequest request);

        void syncTaskStarted(SyncRequest request);

        void syncTaskCompleted(SyncRequest request);
    }

    public void onCreate() {
        worker = new SyncThreadExecutor(mContext, syncManagerCallbacks, queueSize, threadsCount, 5, TimeUnit.MINUTES, requests);
    }

    public void onDestroy() {
        if (requests != null) {
            requests.clear();
        }

        if (worker != null) {
            worker.shutdownNow();
        }
    }

    public void add(final SyncRequest request) {
        worker.addTask(request);
    }

    public void removeDeviceSyncManagerCallback(DeviceSyncManagerCallback syncManagerCallback) {
        if (syncManagerCallback == null) {
            return;
        }

        syncManagerCallbacks.remove(syncManagerCallback);
    }

    public void registerDeviceSyncManagerCallback(DeviceSyncManagerCallback syncManagerCallback) {
        if (syncManagerCallback == null) {
            return;
        }

        syncManagerCallbacks.add(syncManagerCallback);
    }
}
