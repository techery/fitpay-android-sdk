package com.fitpay.android.wearable;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.apdu.ApduPackageResponse;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.interfaces.IControlMessage;
import com.fitpay.android.wearable.interfaces.INotificationMessage;
import com.fitpay.android.wearable.interfaces.ISecureMessage;
import com.fitpay.android.wearable.interfaces.IWearable;
import com.fitpay.android.wearable.listeners.SyncListener;
import com.fitpay.android.wearable.model.ApduPair;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Vlad on 05.04.2016.
 */
public final class SyncManager {
    private SyncListener mListener;
    private List<Subscription> mSubscriptions;

    public SyncManager(@NonNull SyncListener listener) {
        mSubscriptions = new ArrayList<>();
        mListener = listener;
    }

    public void onCreate() {

        mSubscriptions.add(RxBus.getInstance().register(IWearable.class, new Action1<IWearable>() {
            @Override
            public void call(IWearable wearable) {
                if(mListener != null){
                    mListener.onDeviceStateChanged(wearable.getState());
                }
            }
        }));

        mSubscriptions.add(RxBus.getInstance().register(Device.class, new Action1<Device>() {
            @Override
            public void call(Device device) {
                if (mListener != null) {
                    mListener.onDeviceInfoReceived(device);
                }
            }
        }));

        mSubscriptions.add(RxBus.getInstance().register(ISecureMessage.class, new Action1<ISecureMessage>() {
            @Override
            public void call(ISecureMessage secureMessage) {
                if (mListener != null) {
                    mListener.onNFCStateReceived(secureMessage.isNfcEnabled());
                }
            }
        }));

        mSubscriptions.add(RxBus.getInstance().register(INotificationMessage.class, new Action1<INotificationMessage>() {
            @Override
            public void call(INotificationMessage notificationMessage) {
                if (mListener != null) {
                    mListener.onTransactionReceived(notificationMessage.getData());
                }
            }
        }));

        mSubscriptions.add(RxBus.getInstance().register(ApduPair.class, new Action1<ApduPair>() {
            @Override
            public void call(ApduPair pair) {
                if (mListener != null) {
                    mListener.onApduPackageResultReceived(pair);
                }
            }
        }));

        mSubscriptions.add(RxBus.getInstance().register(IControlMessage.class, new Action1<IControlMessage>() {
            @Override
            public void call(IControlMessage controlMessage) {
                if(mListener != null){
                    mListener.onApplicationControlReceived(controlMessage.getData());
                }
            }
        }));
    }

    public void onDestroy() {
        for (Subscription subscription : mSubscriptions) {
            subscription.unsubscribe();
        }

        mSubscriptions.clear();
    }
}
