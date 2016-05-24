package com.fitpay.android.paymentdevice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.callbacks.IListeners;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.impl.ble.BluetoothPaymentDeviceService;
import com.fitpay.android.paymentdevice.impl.mock.MockPaymentDeviceService;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.lang.Class.forName;

/**
 * Connection and synchronization service
 *
 * Allows for service binding or start
 */
public final class DeviceService extends Service {

    private final static String TAG = DeviceService.class.getSimpleName();

    public final static String EXTRA_PAYMENT_SERVICE_TYPE = "PAYMENT_SERVICE_TYPE";
    public final static String EXTRA_PAYMENT_SERVICE_CONFIG = "PAYMENT_SERVICE_CONFIG";
    public final static String PAYMENT_SERVICE_TYPE_MOCK = "PAYMENT_SERVICE_TYPE_MOCK";
    public final static String PAYMENT_SERVICE_TYPE_FITPAY_BLE = "PAYMENT_SERVICE_TYPE_FITPAY_BLE";

    public final static String SYNC_PROPERTY_DEVICE_ID = "syncDeviceId";

    private static final int MAX_REPEATS = 3;

    private IPaymentDeviceService mPaymentDeviceService;
    private String paymentServiceType;
    private String configParams;

    private ErrorPair mErrorRepeats;

    private @Sync.State Integer mSyncEventState;

    private List<Commit> mCommits;
    private Device device;

    private CustomListener mSyncListener = new CustomListener();

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DeviceService getService() {
            return DeviceService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        configure(intent);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        int value = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand.  intent: " + intent);
        if (null == intent) {
            configure(intent);
        }

        return value;
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

    protected void configure(Intent intent) {
        if (null != intent.getExtras() && intent.hasExtra(EXTRA_PAYMENT_SERVICE_TYPE)) {
            paymentServiceType = intent.getExtras().getString(EXTRA_PAYMENT_SERVICE_TYPE);
            if (null != paymentServiceType) {
                switch (paymentServiceType) {
                    case PAYMENT_SERVICE_TYPE_MOCK: {
                        mPaymentDeviceService = new MockPaymentDeviceService();
                        break;
                    }
                    case PAYMENT_SERVICE_TYPE_FITPAY_BLE: {
                        String bluetoothAddress = intent.getExtras().getString(BluetoothPaymentDeviceService.EXTRA_BLUETOOTH_ADDRESS);
                        mPaymentDeviceService = new BluetoothPaymentDeviceService(this, bluetoothAddress);
                        break;
                    }
                    default: {
                        Log.d(TAG, "payment service type is not one of the known types.  type: " + paymentServiceType);
                    }
                }
                if (null == mPaymentDeviceService) {

                    try {
                        Class paymentDeviceServiceClass =  forName(paymentServiceType);
                        mPaymentDeviceService = (IPaymentDeviceService) paymentDeviceServiceClass.newInstance();
                        mPaymentDeviceService.setContext(this);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (null != mPaymentDeviceService && intent.hasExtra(EXTRA_PAYMENT_SERVICE_CONFIG)) {
            configParams = intent.getExtras().getString(EXTRA_PAYMENT_SERVICE_CONFIG);
            Properties props = null;
            try {
                props = convertCommaSeparatedList(configParams);
            } catch (IOException e) {
                Log.e(TAG, "unable to load properties.   Reason: " + e.getMessage());
            }
            if (null != props) {
                mPaymentDeviceService.init(props);
            }
        }
    }

    public String getPaymentServiceType() {
        return paymentServiceType;
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

        // check to see if device has changed, if so close the existing connection
        //TODO should test on device config - more general than MacAddress which is BLE specific (or at least pertinent to Mac devices)
        if (mPaymentDeviceService != null
                && ((mPaymentDeviceService.getMacAddress() == null && paymentDeviceService.getMacAddress() != null)
                    || null != mPaymentDeviceService.getMacAddress() && !mPaymentDeviceService.getMacAddress().equals(paymentDeviceService.getMacAddress()))
                && mPaymentDeviceService.getState() == States.CONNECTED) {
            mPaymentDeviceService.disconnect();
            mPaymentDeviceService.close();
            mPaymentDeviceService = null;
        }

        mPaymentDeviceService = paymentDeviceService;

        pairWithDevice();
    }

    public void pairWithDevice() {

        switch (mPaymentDeviceService.getState()) {
            case States.CONNECTED:
                break;

            case States.INITIALIZED:
                mPaymentDeviceService.connect();
                break;

            case States.DISCONNECTED:
                mPaymentDeviceService.reconnect();
                break;

            default:
                //TODO - why not let device decide if it can connect from this state?
                Logger.e("Can't connect to device.  Current device state does not support the connect operation.  State: " + mPaymentDeviceService.getState());
                break;
        }

    }

    public void readDeviceInfo() {
        if (null == mPaymentDeviceService) {
            //TODO post an error
            Log.e(TAG, "payment device service is not defined.  Can not do operation: readDeviceInfo");
            return;
        }
        if (States.CONNECTED != mPaymentDeviceService.getState()) {
            //TODO post an error
            Log.e(TAG, "payment device service is not connected.  Can not do operation: readDeviceInfo");
            return;
        }
        mPaymentDeviceService.readDeviceInfo();
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
     * This is an asynchronous operation.
     *
     * @param device device object with hypermedia data
     */
    public void syncData(@NonNull Device device) {

        Log.d(TAG, "starting device sync.  device: " + device.getDeviceIdentifier());
        Log.d(TAG, "sync initiated from thread: " + Thread.currentThread() + ", " + Thread.currentThread().getName());

        this.device = device;

        if (mPaymentDeviceService == null) {
            //throw new RuntimeException("You should pair with a payment device at first");
            Logger.e("No payment device connector configured");
            throw new IllegalStateException("No payment device connector configured");
        }

        if (mPaymentDeviceService.getState() != States.CONNECTED) {
            //throw new RuntimeException("You should pair with a payment device at first");
            Logger.e("No payment device connection");
            throw new IllegalStateException("No payment device connection");
        }

        if (mSyncEventState != null &&
                (mSyncEventState == States.STARTED || mSyncEventState == States.IN_PROGRESS)) {
            Logger.w("Sync already in progress. Try again later");
            throw new IllegalStateException("Another sync is currently active.  Please try again later");
        }

        Subscription syncSubscription = getSyncObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getSyncObserver());

    }

    private Observable<Boolean> getSyncObservable() {
        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                syncDevice();
                return aBoolean;
            }
        });
    }


    private void syncDevice() {

        Log.d(TAG, "sync running on thread: " + Thread.currentThread() + ", " + Thread.currentThread().getName());

        // provide sync specific data to device connector
        Properties syncProperties = new Properties();
        syncProperties.put(SYNC_PROPERTY_DEVICE_ID, device.getDeviceIdentifier()) ;
        mPaymentDeviceService.init(syncProperties);
        mPaymentDeviceService.syncInit();

        mErrorRepeats = null;

        NotificationManager.getInstance().addListener(mSyncListener);

        RxBus.getInstance().post(new Sync(States.STARTED));

        DevicePreferenceData deviceData = DevicePreferenceData.load(this, device.getDeviceIdentifier());

        //TODO verify this is not being done on the main thread
        device.getAllCommits(deviceData.getLastCommitId(), new ApiCallback<Collections.CommitsCollection>() {
            @Override
            public void onSuccess(Collections.CommitsCollection result) {

                Log.d(TAG, "processing commits.  count: " + result.getTotalResults());

                mCommits = result.getResults();

                RxBus.getInstance().post(new Sync(States.IN_PROGRESS, mCommits.size()));

                processNextCommit();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Logger.e(errorCode + " " + errorMessage);

                RxBus.getInstance().post(new Sync(States.FAILED));
            }
        });
    }

    private Observer<Boolean> getSyncObserver() {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "sync kickoff completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "sync observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "sync observer onNext: " + bool);
            }
        };
    }


    /**
     * process next commit
     */
    private void processNextCommit(){
        if(mCommits != null && mCommits.size() > 0){
            Commit commit = mCommits.get(0);
            Log.d(TAG, "process commit: " + commit);
            mPaymentDeviceService.processCommit(commit);
            // expose the commit out to others who may want to take action
            RxBus.getInstance().post(commit);
            // TODO remove dead code below - moved into handler
//            Object payload = commit.getPayload();
//            if (payload instanceof ApduPackage) {
//                ApduPackage pkg = (ApduPackage) payload;
//
//                long validUntil = TimestampUtils.getDateForISO8601String(pkg.getValidUntil()).getTime();
//                long currentTime = System.currentTimeMillis();
//
//                if(validUntil > currentTime){
//                    mPaymentDeviceService.executeApduPackage(pkg);
//                } else {
//                    ApduExecutionResult result = new ApduExecutionResult(pkg.getPackageId());
//                    result.setExecutedDuration(0);
//                    result.setExecutedTsEpoch(currentTime);
//                    result.setState(ResponseState.EXPIRED);
//
//                    RxBus.getInstance().post(result);
//                }
//            } else {
//                RxBus.getInstance().post(commit);
//            }
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
            Commit commit = mCommits.get(0);

            commit.confirm(result, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result2) {
                    if (ResponseState.PROCESSED == result.getState()) {
                        RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
                    } else {
                        RxBus.getInstance().post(new CommitFailed((commit.getCommitId())));
                    }
                }

                @Override
                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                    Logger.e("Could not post apduExecutionResult. " + errorCode + ": " + errorMessage);

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
            //TODO remove non-Apdu listener = responsibility moved to PaymentService
           // mCommands.put(Commit.class, data -> onNonApduCommit((Commit) data));
            mCommands.put(CommitSuccess.class, data -> onCommitSuccess((CommitSuccess) data));
            mCommands.put(CommitFailed.class, data -> onCommitFailed((CommitFailed) data));
        }

        @Override
        public void onApduPackageResultReceived(ApduExecutionResult result) {
            sendApduExecutionResult(result);
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
                // retry
                processNextCommit();
            }
        }

        @Override
        public void onSyncStateChanged(Sync syncEvent) {
            Log.d(TAG, "received on sync state changed event: " + syncEvent);
            mSyncEventState = syncEvent.getState();

            if (mSyncEventState == States.COMPLETED || mSyncEventState == States.FAILED) {
                NotificationManager.getInstance().removeListener(mSyncListener);
            }
        }

        @Override
        public void onNonApduCommit(Commit commit) {
            Log.d(TAG, "received non-Apdu commit event: " + commit);
            //TODO just do next commit - needs to be elaborated with event processing
            RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
        }

        @Override
        public void onCommitFailed(CommitFailed commitFailed) {
            Log.d(TAG, "received commit failed event: " + commitFailed.getCommitId());

        }

        @Override
        public void onCommitSuccess(CommitSuccess commitSuccess) {
            Log.d(TAG, "received commit success event.  moving last commit pointer to: " + commitSuccess.getCommitId());
            DevicePreferenceData deviceData = DevicePreferenceData.load(DeviceService.this, DeviceService.this.device.getDeviceIdentifier());
            deviceData.setLastCommitId(commitSuccess.getCommitId());
            DevicePreferenceData.store(DeviceService.this, deviceData);
            Commit commit = mCommits.remove(0);
            processNextCommit();
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

    private Properties convertCommaSeparatedList(String input) throws IOException {
        if (null == input) {
            return null;
        }
            String propertiesFormat = input.replaceAll(",", "\n");
            Properties properties = new Properties();
            properties.load(new StringReader(propertiesFormat));
            return properties;
    }

    public String getConfigString() {
        return configParams;
    }

    public Properties getConfig() {
        Properties props = null;
        try {
            convertCommaSeparatedList(configParams);
        } catch (IOException e) {
            Log.e(TAG, "can not convert config to properties.  Reason: " + e.getMessage());
        }
        return props;
    }

}
