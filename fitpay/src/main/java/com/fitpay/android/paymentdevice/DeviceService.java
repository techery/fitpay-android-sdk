package com.fitpay.android.paymentdevice;

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
import com.fitpay.android.utils.TimestampUtils;
import com.fitpay.android.paymentdevice.callbacks.IListeners;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;
import com.orhanobut.logger.Logger;

import java.util.List;

import me.alexrs.prefs.lib.Prefs;

/**
 * Connection and synchronization service
 */
public final class DeviceService extends Service {

    private static final String KEY_COMMIT_ID = "commitId";
    private static final int MAX_REPEATS = 3;

    private IPaymentDeviceService mPaymentDeviceService;

    private String mLastCommitId;

    private ErrorPair mErrorRepeats;

    private @Sync.State Integer mSyncEventState;

    private List<Commit> mCommits;

    private CustomListener mSyncListener = new CustomListener();

    private final IBinder mBinder = new LocalBinder();

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
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPaymentDeviceService != null) {
            mPaymentDeviceService.close();
            mPaymentDeviceService = null;
        }

        NotificationManager.getInstance().removeListener(mSyncListener);
    }

    /**
     * Get paired payment device
     *
     * @return interface of payment device
     */
    public IPaymentDeviceService getPairedDevice() {
        return mPaymentDeviceService;
    }

    /**
     * Pair with payment device
     *
     * @param paymentDeviceService interface of payment device
     */
    public void pairWithDevice(@NonNull IPaymentDeviceService paymentDeviceService) {

        if (mPaymentDeviceService != null && !mPaymentDeviceService.getMacAddress().equals(paymentDeviceService.getMacAddress()) && mPaymentDeviceService.getState() == States.CONNECTED) {
            mPaymentDeviceService.disconnect();
            mPaymentDeviceService.close();
            mPaymentDeviceService = null;
        }

        mPaymentDeviceService = paymentDeviceService;

        switch (mPaymentDeviceService.getState()) {
            case States.INITIALIZED:
                mPaymentDeviceService.connect();
                break;

            case States.DISCONNECTED:
                mPaymentDeviceService.reconnect();
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
        if (mPaymentDeviceService != null && mPaymentDeviceService.getState() == States.CONNECTED) {
            mPaymentDeviceService.disconnect();
        }
    }

    /**
     * Sync data between FitPay server and payment device
     *
     * @param device device object with hypermedia data
     */
    public void syncData(@NonNull Device device) {

        if (mPaymentDeviceService == null || mPaymentDeviceService.getState() != States.CONNECTED) {
            //throw new RuntimeException("You should pair with a payment device at first");
            Logger.e("You should pair with a payment device at first");
            return;
        }

        if (mSyncEventState != null &&
                (mSyncEventState == States.STARTED || mSyncEventState == States.IN_PROGRESS)) {
            Logger.w("Sync already in progress. Try again later");
            return;
        }

        mErrorRepeats = null;

        NotificationManager.getInstance().addListener(mSyncListener);

        mLastCommitId = Prefs.with(this).getString(KEY_COMMIT_ID, null);

        RxBus.getInstance().post(new Sync(States.STARTED));

        device.getAllCommits(mLastCommitId, new ApiCallback<Collections.CommitsCollection>() {
            @Override
            public void onSuccess(Collections.CommitsCollection result) {

                mCommits = result.getResults();

                int commandsCount = 0;
                for(Commit commit : mCommits){
                    Object payload = commit.getPayload();
                    if (payload instanceof ApduPackage) {
                        ApduPackage pkg = (ApduPackage) payload;
                        commandsCount += pkg.getApduCommands().size();
                    }
                }

                RxBus.getInstance().post(new Sync(States.IN_PROGRESS, commandsCount));

                processNextCommit();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Logger.e(errorCode + " " + errorMessage);

                RxBus.getInstance().post(new Sync(States.FAILED));
            }
        });
    }

    /**
     * process next commit
     */
    private void processNextCommit(){
        if(mCommits != null && mCommits.size() > 0){
            Commit commit = mCommits.get(0);
            Object payload = commit.getPayload();
            if (payload instanceof ApduPackage) {
                ApduPackage pkg = (ApduPackage) payload;

                long validUntil = TimestampUtils.getDateForISO8601String(pkg.getValidUntil()).getTime();
                long currentTime = System.currentTimeMillis();

                if(validUntil > currentTime){
                    mPaymentDeviceService.executeApduPackage(pkg);
                } else {
                    ApduExecutionResult result = new ApduExecutionResult(pkg.getPackageId());
                    result.setExecutedDuration(0);
                    result.setExecutedTsEpoch(currentTime);
                    result.setState(ResponseState.EXPIRED);

                    RxBus.getInstance().post(result);
                }
            } else {
                RxBus.getInstance().post(commit);
            }
        } else {
            RxBus.getInstance().post(new Sync(States.COMPLETED));
        }
    }

    /**
     * Send apdu execution result to the server
     * @param result apdu execution result
     */
    private void sendApduExecutionResult(ApduExecutionResult result){
        if(mCommits != null && mCommits.size() > 0){
            Commit commit = mCommits.remove(0);

            commit.confirm(result, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    mLastCommitId = commit.getCommitId();

                    Prefs.with(DeviceService.this).save(KEY_COMMIT_ID, mLastCommitId);
                }

                @Override
                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                    Logger.e(errorCode + " " + errorMessage);

                    RxBus.getInstance().post(new Sync(States.FAILED));
                }
            });
        }
    }

    /**
     * Apdu and Sync callbacks
     */
    private class CustomListener extends Listener implements IListeners.ApduListener, IListeners.SyncListener{

        private CustomListener(){
            super();
            mCommands.put(ApduExecutionResult.class, data -> {
                ApduExecutionResult result = (ApduExecutionResult) data;

                switch (result.getState()){
                    case ResponseState.ERROR:
                        onApduPackageErrorReceived(result);
                        break;

                    default:
                        onApduPackageResultReceived(result);
                        break;
                }
            });
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        @Override
        public void onApduPackageResultReceived(ApduExecutionResult result) {
            sendApduExecutionResult(result);
            processNextCommit();
        }

        @Override
        public void onApduPackageErrorReceived(ApduExecutionResult result) {

            final String id = result.getPackageId();

            if(mErrorRepeats == null || !mErrorRepeats.first.equals(id)){
                mErrorRepeats = new ErrorPair(id, 0);
            }

            if(++mErrorRepeats.second == MAX_REPEATS){
                sendApduExecutionResult(result);
            } else {
                processNextCommit();
            }
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

    private class ErrorPair{
        String first;
        int second;

        ErrorPair(String first, int second){
            this.first = first;
            this.second = second;
        }
    }
}
