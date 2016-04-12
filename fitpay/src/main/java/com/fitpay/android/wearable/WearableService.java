package com.fitpay.android.wearable;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.callbacks.IListeners;
import com.fitpay.android.wearable.constants.States;
import com.fitpay.android.wearable.enums.Sync;
import com.fitpay.android.wearable.interfaces.IApduMessage;
import com.fitpay.android.wearable.interfaces.IWearable;
import com.orhanobut.logger.Logger;

import java.util.List;

import me.alexrs.prefs.lib.Prefs;

/**
 * Connection and synchronization service
 */
public final class WearableService extends Service {

    private static final String KEY_COMMIT_ID = "commitId";

    private IWearable mWearable;

    private String mLastCommitId;

    private @Sync.State Integer mSyncEventState;

    private List<Commit> mCommits;

    private CustomListener mSyncListener = new CustomListener();

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
            mWearable = null;
        }

        NotificationManager.getInstance().removeListener(mSyncListener);
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

        if (mWearable != null && !mWearable.getMacAddress().equals(wearable.getMacAddress()) && mWearable.getState() == States.CONNECTED) {
            mWearable.disconnect();
            mWearable.close();
            mWearable = null;
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
     * @param device device object with hypermedia data
     */
    public void syncData(@NonNull Device device) {

        if (mWearable == null || mWearable.getState() != States.CONNECTED) {
            //throw new RuntimeException("You should pair with a payment device at first");
            Logger.e("You should pair with a payment device at first");
            return;
        }

        if (mSyncEventState != null &&
                (mSyncEventState == States.STARTED || mSyncEventState == States.IN_PROGRESS)) {
            Logger.w("Sync already in progress. Try again later");
            return;
        }

        NotificationManager.getInstance().addListener(mSyncListener);

        mLastCommitId = Prefs.with(this).getString(KEY_COMMIT_ID, null);

        RxBus.getInstance().post(new Sync(States.STARTED));

        device.getAllCommits(mLastCommitId, new ApiCallback<Collections.CommitsCollection>() {
            @Override
            public void onSuccess(Collections.CommitsCollection result) {

                RxBus.getInstance().post(new Sync(States.IN_PROGRESS));

                mCommits = result.getResults();

                processNextCommit();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Logger.e(errorCode + " " + errorMessage);

                RxBus.getInstance().post(new Sync(States.FAILED));
            }
        });
    }

    private void processNextCommit(){
        if(mCommits != null && mCommits.size() > 0){
            Commit commit = mCommits.get(0);
            Object payload = commit.getPayload();
            if (payload instanceof ApduPackage) {
                ApduPackage pkg = (ApduPackage) payload;
                mWearable.executeApduPackage(pkg);
            } else {
                RxBus.getInstance().post(commit);
            }
        } else {
            RxBus.getInstance().post(new Sync(States.COMPLETED));
        }
    }

    private class CustomListener extends Listener implements IListeners.ApduListener, IListeners.SyncListener{

        private CustomListener(){
            super();
            mCommands.put(IApduMessage.class, data -> {
                ApduExecutionResult result = (ApduExecutionResult) data;

                switch (result.getState()){
                    case ResponseState.PROCESSED:
                        onApduPackageResultReceived(result);
                        break;

                    default:
                        onApduPackageErrorReceived(result);
                        break;
                }
            });
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        @Override
        public void onApduPackageResultReceived(ApduExecutionResult result) {

            if(mCommits != null && mCommits.size() > 0){
                Commit commit = mCommits.remove(0);

                commit.confirm(result, new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        mLastCommitId = commit.getCommitId();

                        Prefs.with(WearableService.this).save(KEY_COMMIT_ID, mLastCommitId);
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        Logger.e(errorCode + " " + errorMessage);

                        RxBus.getInstance().post(new Sync(States.FAILED));
                    }
                });
            }

            processNextCommit();
        }

        @Override
        public void onApduPackageErrorReceived(ApduExecutionResult result) {
            RxBus.getInstance().post(new Sync(States.FAILED));
        }

        @Override
        public void onSyncStateChanged(Sync syncEvent) {
            mSyncEventState = syncEvent.getState();

            if (mSyncEventState == States.COMPLETED || mSyncEventState == States.FAILED) {
                NotificationManager.getInstance().removeListener(mSyncListener);
            }
        }

        @Override
        public void onNonApduCommit(Commit commit) {

        }
    }
}
