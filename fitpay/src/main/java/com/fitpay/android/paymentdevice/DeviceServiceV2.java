package com.fitpay.android.paymentdevice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;

import static android.content.ContentValues.TAG;

/**
 * Connection and synchronization service v2
 * <p>
 * Allows for service binding or start
 */
public final class DeviceServiceV2 extends Service {

    private final IBinder mBinder = new LocalBinder();

    private DeviceSyncManagerV2 syncManager;

    private MessageListener mSyncListener = new MessageListener();


    public static void run(Context context) {
        context.startService(new Intent(context, DeviceServiceV2.class));
    }


    public static void stop(Context context) {
        context.stopService(new Intent(context, DeviceServiceV2.class));
    }


    public class LocalBinder extends Binder {
        public DeviceServiceV2 getService() {
            return DeviceServiceV2.this;
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

        syncManager = new DeviceSyncManagerV2(this);
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
            mCommands.put(SyncRequest.class, data -> {
                if (syncManager != null) {
                    syncManager.add((SyncRequest) data);
                } else {
                    Log.e(TAG, "syncManager is null");
                }
            });
        }
    }
}
