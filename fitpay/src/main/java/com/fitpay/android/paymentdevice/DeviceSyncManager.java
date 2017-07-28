package com.fitpay.android.paymentdevice;

import android.content.Context;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.paymentdevice.utils.sync.SyncThreadExecutor;
import com.fitpay.android.utils.FPLog;

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
        threadsCount = 4;//Integer.parseInt(ApiManager.getConfig().get(ApiManager.PROPERTY_SYNC_THREADS_COUNT));
        requests = new ArrayBlockingQueue<>(queueSize);
    }

    /**
     * This interface is more about testing and allowing unit tests to inject hooks into the process making this
     * class more testable.
     */
    public interface DeviceSyncManagerCallback {
        void syncRequestAdded(SyncRequest request);

        void syncTaskStarting(SyncRequest request);

        void syncTaskStarted(SyncRequest request);

        void syncTaskCompleted(SyncRequest request);
    }

    public void onCreate() {
        worker = new SyncThreadExecutor(mContext, syncManagerCallbacks, threadsCount, threadsCount, 60L, TimeUnit.SECONDS, requests);
//        worker = new SyncWorkerThread(mContext, requests);
//        worker.setName("DeviceSyncManagerWorkerThread");
//        worker.setPriority(Thread.MIN_PRIORITY);
//        worker.start();
    }

    public void onDestroy() {
        if (requests != null) {
            requests.clear();
        }

        if (worker != null) {
//            try {
            worker.shutdownNow();
//            try {
//                worker.awaitTermination(1, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//                worker.join();
//            } catch (InterruptedException e) {
//                FPLog.e("error while waiting for sync worker to properly shutdown", e);
//            }-
        }
    }

    public void add(final SyncRequest request) {
        if (request == null) {
            return;
        }

        worker.addTask(request);

        FPLog.d("added sync request to queue for processing, current queue size [" + requests.size() + "]: " + request);

        for (DeviceSyncManagerCallback callback : syncManagerCallbacks) {
            callback.syncRequestAdded(request);
        }
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
