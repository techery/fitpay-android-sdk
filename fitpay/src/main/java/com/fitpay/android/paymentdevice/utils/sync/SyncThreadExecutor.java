package com.fitpay.android.paymentdevice.utils.sync;

import android.content.Context;

import com.fitpay.android.paymentdevice.DeviceSyncManager;
import com.fitpay.android.paymentdevice.models.SyncRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vlad on 24.07.2017.
 */

public class SyncThreadExecutor extends ThreadPoolExecutor {

    private final HashMap<String, BlockingQueue<SyncRequest>> syncBuffer;

    private final List<String> inWork = new ArrayList<>();

    private final Context mContext;
    private final List<DeviceSyncManager.DeviceSyncManagerCallback> syncManagerCallbacks;

    public SyncThreadExecutor(Context context, List<DeviceSyncManager.DeviceSyncManagerCallback> syncManagerCallbacks, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.mContext = context;
        this.syncManagerCallbacks = syncManagerCallbacks;
//        this.workQueue = workQueue;
        this.syncBuffer = new HashMap<>();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        SyncWorkerTask task = (SyncWorkerTask) r;
        inWork.add(task.syncRequest.getDevice().getDeviceIdentifier());
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        SyncWorkerTask task = (SyncWorkerTask) r;
        inWork.remove(task.syncRequest.getDevice().getDeviceIdentifier());

        for (String key : syncBuffer.keySet()) {
            if (!inWork.contains(key)) {
                execute(new SyncWorkerTask(mContext, syncManagerCallbacks, syncBuffer.get(key).poll()));
            }
            break;
        }

        super.afterExecute(r, t);
    }

    public void addTask(SyncRequest request) {
        String deviceId = request.getDevice().getDeviceIdentifier();
        if (inWork.contains(deviceId)) {
            syncBuffer.get(deviceId).add(request);
        } else {
            execute(new SyncWorkerTask(mContext, syncManagerCallbacks, request));
        }
    }
}
