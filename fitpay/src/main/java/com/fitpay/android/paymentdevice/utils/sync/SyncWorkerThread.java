package com.fitpay.android.paymentdevice.utils.sync;

import android.content.Context;

import com.fitpay.android.paymentdevice.DeviceSyncManager;
import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.utils.FPLog;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This thread is responsible for listening for requests to sync, then launching a {@link SyncWorkerTask} to perform the
 * actual work of one individual sync.
 */
public final class SyncWorkerThread extends Thread {
    private final static String TAG = SyncWorkerThread.class.getSimpleName();

    private final Context mContext;
    private final ScheduledExecutorService timeoutWatcherExecutor;

    private final BlockingQueue<SyncRequest> requests;
    private volatile boolean running = true;

    private List<DeviceSyncManager.DeviceSyncManagerCallback> syncManagerCallbacks;

    public SyncWorkerThread(Context mContext, List<DeviceSyncManager.DeviceSyncManagerCallback> syncManagerCallbacks, BlockingQueue<SyncRequest> requests) {
        this.requests = requests;
        this.mContext = mContext;
        this.syncManagerCallbacks = syncManagerCallbacks;
        this.timeoutWatcherExecutor = Executors.newScheduledThreadPool(1);
    }

    public void run() {
        while (running) {
            try {
                FPLog.d(TAG, "waiting for new sync request");
                SyncRequest syncRequest = requests.take();

                FPLog.d(TAG, "sync request received, launching sync task: " + syncRequest);

                for (DeviceSyncManager.DeviceSyncManagerCallback callback : syncManagerCallbacks) {
                    callback.syncTaskStarting(syncRequest);
                }

                final long startTime = System.currentTimeMillis();
//                SyncWorkerTask task = new SyncWorkerTask(mContext, syncManagerCallbacks, timeoutWatcherExecutor, syncRequest);
//                task.run();

                FPLog.i("syncRequest: " + syncRequest + " completed in " + (System.currentTimeMillis() - startTime) + "ms");

                for (DeviceSyncManager.DeviceSyncManagerCallback callback : syncManagerCallbacks) {
                    callback.syncTaskCompleted(syncRequest);
                }
            } catch (InterruptedException e) {
                FPLog.d("sync worker thread interrupted, shutting down");
                running = false;
            }
        }
    }

    public void shutdown() {
        FPLog.i(TAG, "sync worker thread has been requested to shutdown");
        running = false;
        interrupt();
    }
}