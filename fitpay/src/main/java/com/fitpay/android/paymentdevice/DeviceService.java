package com.fitpay.android.paymentdevice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;

/**
 * Connection and synchronization service
 * <p>
 * Allows for service binding or start
 */
public final class DeviceService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private DeviceSyncManager syncManager;

    private MessageListener mSyncListener = new MessageListener();


    public static void run(Context context) {
        context.startService(new Intent(context, DeviceService.class));
    }


    public static void stop(Context context) {
        context.stopService(new Intent(context, DeviceService.class));
    }


    public class LocalBinder extends Binder {
        public DeviceService getService() {
            return DeviceService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        stopSelf();
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        syncManager = new DeviceSyncManager(this);
        syncManager.onCreate();

        NotificationManager.getInstance().addListener(mSyncListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (syncManager != null) {
            syncManager.onDestroy();
            syncManager = null;
        }

        NotificationManager.getInstance().removeListener(mSyncListener);
    }

    /**
     * Listen to Apdu and Sync callbacks
     */
    private class MessageListener extends Listener {

        private MessageListener() {
            super();
            mCommands.put(SyncRequest.class, data -> syncManager.add((SyncRequest) data));
        }
    }
}
