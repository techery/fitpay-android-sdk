package com.fitpay.android.wearable;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.callbacks.WearableListener;
import com.fitpay.android.wearable.enums.States;
import com.fitpay.android.wearable.enums.SyncEvent;
import com.fitpay.android.wearable.interfaces.IWearable;
import com.fitpay.android.wearable.utils.ApduPair;
import com.orhanobut.logger.Logger;

import java.util.HashSet;

import me.alexrs.prefs.lib.Prefs;

public final class WearableService extends Service {

    private static final String KEY_COMMIT_ID = "commitId";

    private IWearable mWearable;
    private Device mDevice;

    private String mLastCommitId;

    private HashSet<ApduPackage> mSyncSet = new HashSet<>();

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WearableService getService() {
            return WearableService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWearable != null) {
            mWearable.close();
        }
    }

    /**
     * Get paired payment device
     *
     * @return interface of payment device
     */
    public IWearable getPairedDevice() {
        return mWearable;
    }

    /**
     * Pair with payment device
     *
     * @param wearable interface of payment device
     */
    public void pairWithDevice(@NonNull IWearable wearable) {

        if (mWearable != null
                && !mWearable.getMacAddress().equals(wearable.getMacAddress())
                && mWearable.getState() == States.CONNECTED) {
            mWearable.disconnect();
            mWearable.close();
            mWearable = null;
        } else {
            return;
        }

        mWearable = wearable;

        switch (mWearable.getState()) {
            case States.INITIALIZED:
                mWearable.connect();
                break;

            case States.DISCONNECTED:
                mWearable.reconnect();
                break;

            default:
                Logger.e("Can't connect to device");
                break;
        }
    }


    /**
     * Disconnect from payment device
     */
    public void disconnect() {
        if (mWearable != null && mWearable.getState() == States.CONNECTED) {
            mWearable.disconnect();
        }
    }

    /**
     * Sync data between FitPay server and payment device
     *
     * @param device hypermedia device
     */
    public void syncData(Device device) {

        if (mWearable == null) {
            throw new NullPointerException("You should pair with a payment device at first");
        }

        NotificationManager.getInstance().addWearableListener(mWearableListener);

        mLastCommitId = Prefs.with(this).getString(KEY_COMMIT_ID, null);

        RxBus.getInstance().post(new SyncEvent(SyncEvent.STARTED));

        mDevice = device;
        mDevice.getCommits(mLastCommitId, new ApiCallback<Collections.CommitsCollection>() {
            @Override
            public void onSuccess(Collections.CommitsCollection result) {

                RxBus.getInstance().post(new SyncEvent(SyncEvent.IN_PROGRESS));

                for (Commit commit : result.getResults()) {
                    Object payload = commit.getPayload();
                    if (payload instanceof ApduPackage) {
                        ApduPackage pkg = (ApduPackage) payload;
                        mWearable.sendApduPackage(pkg);
                        mSyncSet.add(pkg);
                    }
                }

                checkSyncForComplete();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Logger.e(errorCode + " " + errorMessage);

                RxBus.getInstance().post(new SyncEvent(SyncEvent.FAILED));
            }
        });
    }

    private void checkSyncForComplete() {
        if (mSyncSet.size() == 0) {
            NotificationManager.getInstance().removeWearableListener(mWearableListener);
            RxBus.getInstance().post(new SyncEvent(SyncEvent.COMPLETED));
        }
    }

    private WearableListener mWearableListener = new WearableListener() {
        @Override
        public void onDeviceStateChanged(@States.Wearable int state) {

        }

        @Override
        public void onDeviceInfoReceived(Device device) {

        }

        @Override
        public void onNFCStateReceived(boolean isEnabled) {

        }

        @Override
        public void onTransactionReceived(byte[] data) {

        }

        @Override
        public void onApduPackageResultReceived(final ApduPair pair) {
            if (mSyncSet.contains(pair.first)){
                mSyncSet.remove(pair.first);

                pair.first.confirm(pair.second, new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        mLastCommitId = pair.first.getSeId();
                        Prefs.with(WearableService.this).save(KEY_COMMIT_ID, mLastCommitId);

                        checkSyncForComplete();
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        Logger.e(errorMessage);
                    }
                });
            }
        }

        @Override
        public void onApplicationControlReceived(byte[] data) {

        }
    };
}
