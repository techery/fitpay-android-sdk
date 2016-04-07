package com.fitpay.android.wearable;

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.callbacks.SyncListener;
import com.fitpay.android.wearable.interfaces.IControlMessage;
import com.fitpay.android.wearable.interfaces.INotificationMessage;
import com.fitpay.android.wearable.interfaces.ISecureMessage;
import com.fitpay.android.wearable.interfaces.IWearable;
import com.fitpay.android.wearable.callbacks.WearableListener;
import com.fitpay.android.wearable.utils.ApduPair;
import com.fitpay.android.wearable.enums.SyncEvent;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Vlad on 06.04.2016.
 */
public final class NotificationManager {

    private static NotificationManager sInstance;

    private List<WearableListener> mWearableListeners;
    private List<Subscription> mWearableSubscriptions;

    private List<SyncListener> mSyncListeners;
    private List<Subscription> mSyncSubscriptions;

    public static NotificationManager getInstance() {
        if (sInstance == null) {
            synchronized (NotificationManager.class) {
                if (sInstance == null) {
                    sInstance = new NotificationManager();
                }
            }
        }

        return sInstance;
    }

    private NotificationManager() {
        mWearableSubscriptions = new ArrayList<>();
        mWearableListeners = new ArrayList<>();

        mSyncListeners = new ArrayList<>();
        mSyncSubscriptions = new ArrayList<>();
    }

    private void subscribeToWearable(){
        mWearableSubscriptions.add(RxBus.getInstance().register(IWearable.class, new Action1<IWearable>() {
            @Override
            public void call(IWearable wearable) {
                for(WearableListener listener : mWearableListeners) {
                    listener.onDeviceStateChanged(wearable.getState());
                }
            }
        }));

        mWearableSubscriptions.add(RxBus.getInstance().register(Device.class, new Action1<Device>() {
            @Override
            public void call(Device device) {
                for(WearableListener listener : mWearableListeners) {
                    listener.onDeviceInfoReceived(device);
                }
            }
        }));

        mWearableSubscriptions.add(RxBus.getInstance().register(ISecureMessage.class, new Action1<ISecureMessage>() {
            @Override
            public void call(ISecureMessage secureMessage) {
                for(WearableListener listener : mWearableListeners) {
                    listener.onNFCStateReceived(secureMessage.isNfcEnabled());
                }
            }
        }));

        mWearableSubscriptions.add(RxBus.getInstance().register(INotificationMessage.class, new Action1<INotificationMessage>() {
            @Override
            public void call(INotificationMessage notificationMessage) {
                for(WearableListener listener : mWearableListeners) {
                    listener.onTransactionReceived(notificationMessage.getData());
                }
            }
        }));

        mWearableSubscriptions.add(RxBus.getInstance().register(ApduPair.class, new Action1<ApduPair>() {
            @Override
            public void call(ApduPair pair) {
                for(WearableListener listener : mWearableListeners) {
                    listener.onApduPackageResultReceived(pair);
                }
            }
        }));

        mWearableSubscriptions.add(RxBus.getInstance().register(IControlMessage.class, new Action1<IControlMessage>() {
            @Override
            public void call(IControlMessage controlMessage) {
                for(WearableListener listener : mWearableListeners) {
                    listener.onApplicationControlReceived(controlMessage.getData());
                }
            }
        }));
    }

    private void unsubscribeFromWearable() {
        for (Subscription subscription : mWearableSubscriptions) {
            subscription.unsubscribe();
            subscription = null;
        }

        mWearableSubscriptions.clear();
    }

    private void subscribeToSync(){
        mSyncSubscriptions.add(RxBus.getInstance().register(SyncEvent.class, new Action1<SyncEvent>() {
            @Override
            public void call(SyncEvent syncEvent) {
                for(SyncListener syncListener : mSyncListeners){
                    syncListener.onSyncStateChanged(syncEvent.getState());
                }
            }
        }));

        mSyncSubscriptions.add(RxBus.getInstance().register(Commit.class, new Action1<Commit>() {
            @Override
            public void call(Commit commit) {
                for(SyncListener syncListener : mSyncListeners){
                    syncListener.onNonApduCommit(commit);
                }
            }
        }));

        mSyncSubscriptions.add(RxBus.getInstance().register(ApduPair.class, new Action1<ApduPair>() {
            @Override
            public void call(ApduPair pair) {
                for(SyncListener listener : mSyncListeners) {
                    listener.onApduPackageResultReceived(pair);
                }
            }
        }));
    }

    private void unsubscribeFromSync(){
        for (Subscription subscription : mSyncSubscriptions) {
            subscription.unsubscribe();
            subscription = null;
        }

        mSyncSubscriptions.clear();
    }

    public void addWearableListener(WearableListener listener){
        if(mWearableListeners.size() == 0){
            subscribeToWearable();
        }

        if(!mWearableListeners.contains(listener)) {
            mWearableListeners.add(listener);
        }
    }

    public void removeWearableListener(WearableListener listener){
        mWearableListeners.remove(listener);

        if(mWearableListeners.size() == 0){
            unsubscribeFromWearable();
        }
    }

    public void addSyncListener(SyncListener listener){
        if(mSyncListeners.size() == 0){
            subscribeToSync();
        }

        if(!mSyncListeners.contains(listener)) {
            mSyncListeners.add(listener);
        }
    }

    public void removeSyncListener(SyncListener listener){
        mSyncListeners.remove(listener);

        if(mSyncListeners.size() == 0){
            unsubscribeFromSync();
        }
    }
}
