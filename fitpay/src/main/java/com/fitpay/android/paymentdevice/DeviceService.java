package com.fitpay.android.paymentdevice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fitpay.android.api.callbacks.ResultProvidingCallback;
import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.Payload;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.callbacks.IListeners;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.impl.ble.BluetoothPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.impl.mock.MockPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

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

    private IPaymentDeviceConnector paymentDeviceConnector;
    private String paymentDeviceConnectorType;

    private String configParams;


    private @Sync.State Integer mSyncEventState;

    private List<Commit> mCommits;
    private Device device;

    private CustomListener mSyncListener = new CustomListener();

    private final IBinder mBinder = new LocalBinder();

    Executor executor = Executors.newSingleThreadExecutor();

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

        if (paymentDeviceConnector != null) {
            paymentDeviceConnector.close();
            paymentDeviceConnector = null;
        }

        NotificationManager.getInstance().removeListener(mSyncListener);
    }

    /**
     * Used for retrieving connector config information
     *
     * @return name of connector class
     */
    public String getPaymentServiceType() {
        if (null == paymentDeviceConnectorType) {
            if (null != paymentDeviceConnector) {
                paymentDeviceConnectorType = paymentDeviceConnector.getClass().getName();
            }
        }
        return paymentDeviceConnectorType;
    }


    protected void configure(Intent intent) {
        if (null == intent) {
            Log.d(TAG, "DeviceService can not be configured with a null Intent.  Current connector: " + paymentDeviceConnector);
            return;
        }
        if (null != intent.getExtras() && intent.hasExtra(EXTRA_PAYMENT_SERVICE_TYPE)) {
            paymentDeviceConnectorType = intent.getExtras().getString(EXTRA_PAYMENT_SERVICE_TYPE);
            if (null != paymentDeviceConnectorType) {
                switch (paymentDeviceConnectorType) {
                    case PAYMENT_SERVICE_TYPE_MOCK: {
                        paymentDeviceConnector = new MockPaymentDeviceConnector();
                        break;
                    }
                    case PAYMENT_SERVICE_TYPE_FITPAY_BLE: {
                        String bluetoothAddress = intent.getExtras().getString(BluetoothPaymentDeviceConnector.EXTRA_BLUETOOTH_ADDRESS);
                        paymentDeviceConnector = new BluetoothPaymentDeviceConnector(this, bluetoothAddress);
                        break;
                    }
                    default: {
                        Log.d(TAG, "payment service type is not one of the known types.  type: " + paymentDeviceConnectorType);
                    }
                }
                if (null == paymentDeviceConnector) {

                    try {
                        Class paymentDeviceConnectorClass =  forName(paymentDeviceConnectorType);
                        paymentDeviceConnector = (IPaymentDeviceConnector) paymentDeviceConnectorClass.newInstance();
                        paymentDeviceConnector.setContext(this);
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
        if (null != paymentDeviceConnector && intent.hasExtra(EXTRA_PAYMENT_SERVICE_CONFIG)) {
            configParams = intent.getExtras().getString(EXTRA_PAYMENT_SERVICE_CONFIG);
            Properties props = null;
            try {
                props = convertCommaSeparatedList(configParams);
            } catch (IOException e) {
                Log.e(TAG, "unable to load properties.   Reason: " + e.getMessage());
            }
            if (null != props) {
                paymentDeviceConnector.init(props);
            }
        }
        if (null != paymentDeviceConnector) {
            paymentDeviceConnector.reset();
        }
    }

    /**
     * Get paired payment device
     *
     * @param paymentDeviceConnector the payment device
     */
    public void setPaymentDeviceConnector(IPaymentDeviceConnector paymentDeviceConnector) {
        // check to see if device has changed, if so close the existing connection
        //TODO should test on device config - more general than MacAddress which is BLE specific (or at least pertinent to Mac devices)
        if (this.paymentDeviceConnector != null
                && ((this.paymentDeviceConnector.getMacAddress() == null && paymentDeviceConnector.getMacAddress() != null)
                || null != this.paymentDeviceConnector.getMacAddress() && !this.paymentDeviceConnector.getMacAddress().equals(paymentDeviceConnector.getMacAddress()))
                && this.paymentDeviceConnector.getState() == States.CONNECTED) {
            this.paymentDeviceConnector.disconnect();
            this.paymentDeviceConnector.close();
            this.paymentDeviceConnector = null;
        }
        this.paymentDeviceConnector = paymentDeviceConnector;
    }


    /**
     * Get device connector
     *
     * @return interface of payment device
     */
    public IPaymentDeviceConnector getPaymentDeviceConnector() {
        return paymentDeviceConnector;
    }

    public void connectToDevice() {

        if (null == paymentDeviceConnector) {
            throw new IllegalStateException("Payment device connector has not been configured");
        }

        switch (paymentDeviceConnector.getState()) {
            case States.CONNECTED:
                break;

            case States.INITIALIZED:
                paymentDeviceConnector.connect();
                break;

            case States.DISCONNECTED:
                paymentDeviceConnector.reconnect();
                break;

            default:
                //TODO - why not let device decide if it can connect from this state?
                Logger.e("Can't connect to device.  Current device state does not support the connect operation.  State: " + paymentDeviceConnector.getState());
                break;
        }

    }

    public void readDeviceInfo() {
        if (null == paymentDeviceConnector) {
            //TODO post an error
            Log.e(TAG, "payment device service is not defined.  Can not do operation: readDeviceInfo");
            return;
        }
        if (States.CONNECTED != paymentDeviceConnector.getState()) {
            //TODO post an error
            Log.e(TAG, "payment device service is not connected.  Can not do operation: readDeviceInfo");
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                paymentDeviceConnector.readDeviceInfo();
            }
        });
    }


    /**
     * Disconnect from payment device
     */
    public void disconnect() {
        if (paymentDeviceConnector != null && paymentDeviceConnector.getState() != States.DISCONNECTED) {
            paymentDeviceConnector.disconnect();
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

        if (paymentDeviceConnector == null) {
            //throw new RuntimeException("You should pair with a payment device at first");
            Logger.e("No payment device connector configured");
            throw new IllegalStateException("No payment device connector configured");
        }

        if (paymentDeviceConnector.getState() != States.CONNECTED) {
            //throw new RuntimeException("You should pair with a payment device at first");
            Logger.e("No payment device connection");
            throw new IllegalStateException("No payment device connection");
        }

        if (mSyncEventState != null &&
                (mSyncEventState == States.STARTED || mSyncEventState == States.IN_PROGRESS)) {
            Logger.w("Sync already in progress. Try again later");
            throw new IllegalStateException("Another sync is currently active.  Please try again later");
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                syncDevice();
            }
        });

//
//        Subscription syncSubscription = getSyncObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.newThread())
//                .subscribe(getSyncObserver());

    }


    //TODO remove
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
        paymentDeviceConnector.init(syncProperties);
        paymentDeviceConnector.syncInit();

        NotificationManager.getInstance().addListenerToCurrentThread(mSyncListener);

        RxBus.getInstance().post(new Sync(States.STARTED));

        DevicePreferenceData deviceData = DevicePreferenceData.load(this, device.getDeviceIdentifier());

        //TODO getAllCommits callback is done on UI Thread - make change
        // since we do not want processing to be done on UI Thread need to make it a blocking call

        //TODO remove apdu testing features
        boolean testApdus = false;

        /*
         * getAllCommits callback will revert to UI thread.   To continue the process on
         * background thread, the callback blocks, waits for a result, and provides it back to the
         * current thread of execution
         */

        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Collections.CommitsCollection> callback = new ResultProvidingCallback<>(latch);
        device.getAllCommits(deviceData.getLastCommitId(), callback);

        //TODO make callback timeout configurable?
        try {
            latch.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //party on
        }
        if (null != callback.getResult()) {

                mCommits = callback.getResult().getResults();

                //TODO remove this test code:  This code is for testing of apdus only - remove for production
                if (testApdus) {
                    Log.w(TAG, "adding apdu command for testing purposes");
                    Gson gson = new Gson();
                    ApduPackage apduPackage = gson.fromJson(getTestApduPackage(), ApduPackage.class);
                    Commit commit = new Commit.Builder()
                            .commitId(UUID.randomUUID().toString())
                            .commitType(CommitTypes.APDU_PACKAGE)
                            .createdTs(System.currentTimeMillis())
                            .payload(new Payload(apduPackage))
                            .build();
                    mCommits.add(commit);
                }

            Log.d(TAG, "processing commits.  count: " + callback.getResult().getTotalResults());
            RxBus.getInstance().post(new Sync(States.IN_PROGRESS, mCommits.size()));
            processNextCommit();
        } else {
                Log.e(TAG, "get commits failed.  reasonCode: " + callback.getErrorCode() + ",  " + callback.getErrorMessage());
                RxBus.getInstance().post(new Sync(States.FAILED));
        }
    }

    //TODO remove
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
        if(mCommits != null && mCommits.size() > 0) {
            RxBus.getInstance().post(new Sync(States.INC_PROGRESS, mCommits.size()));
            Commit commit = mCommits.get(0);
            Log.d(TAG, "process commit: " + commit + " on thread: " + Thread.currentThread());
            paymentDeviceConnector.processCommit(commit);
            // expose the commit out to others who may want to take action
            RxBus.getInstance().post(commit);
        } else {
            RxBus.getInstance().post(new Sync(States.COMPLETED));
        }
    }

    /**
     * Apdu and Sync callbacks
     */
    private class CustomListener extends Listener implements IListeners.SyncListener{

        private CustomListener(){
            super();
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
            mCommands.put(CommitSuccess.class, data -> onCommitSuccess((CommitSuccess) data));
            mCommands.put(CommitFailed.class, data -> onCommitFailed((CommitFailed) data));
        }

        @Override
        public void onSyncStateChanged(Sync syncEvent) {
            Log.d(TAG, "received on sync state changed event: " + syncEvent);
            mSyncEventState = syncEvent.getState();

            if (mSyncEventState == States.COMPLETED || mSyncEventState == States.FAILED) {
                NotificationManager.getInstance().removeListener(mSyncListener);
            }

            if (mSyncEventState == States.COMMIT_COMPLETED) {
                processNextCommit();
            }
        }

        @Override
        public void onCommitFailed(CommitFailed commitFailed) {
            Log.d(TAG, "received commit failed event: " + commitFailed.getCommitId());
            RxBus.getInstance().post(new Sync(States.FAILED));
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


    //TODO remove this test code after completing apdu testing
    private String getTestApduPackage() {

        String apduJson = "{  \n" +
                "   \"seIdType\":\"iccid\",\n" +
                "   \"targetDeviceType\":\"fitpay.gandd.model.Device\",\n" +
                "   \"targetDeviceId\":\"72425c1e-3a17-4e1a-b0a4-a41ffcd00a5a\",\n" +
                "   \"packageId\":\"baff08fb-0b73-5019-8877-7c490a43dc64\",\n" +
                "   \"seId\":\"333274689f09352405792e9493356ac880c44444442\",\n" +
                "   \"targetAid\":\"8050200008CF0AFB2A88611AD51C\",\n" +
                "   \"commandApdus\":[  \n" +
                "      {  \n" +
                "         \"commandId\":\"5f2acf6f-536d-4444-9cf4-7c83fdf394bf\",\n" +
                "         \"groupId\":0,\n" +
                "         \"sequence\":0,\n" +
                "         \"command\":\"00E01234567890ABCDEF\",\n" +
                "         \"type\":\"CREATE FILE\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"00df5f39-7627-447d-9380-46d8574e0643\",\n" +
                "         \"groupId\":1,\n" +
                "         \"sequence\":1,\n" +
                "         \"command\":\"8050200008CF0AFB2A88611AD51C\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"9c719928-8bb0-459c-b7c0-2bc48ec53f3c\",\n" +
                "         \"groupId\":1,\n" +
                "         \"sequence\":2,\n" +
                "         \"command\":\"84820300106BBC29E6A224522E83A9B26FD456111500\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"b148bea5-6d98-4c83-8a20-575b4edd7a42\",\n" +
                "         \"groupId\":1,\n" +
                "         \"sequence\":3,\n" +
                "         \"command\":\"9800E01234567890ABCDEF84820300106BBC29E6A224522E83A9B26FD456111500\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"905fc5ab-4b15-4704-889b-2c5ffcfb2d68\",\n" +
                "         \"groupId\":2,\n" +
                "         \"sequence\":4,\n" +
                "         \"command\":\"84F2200210F25397DCFB728E25FBEE52E748A116A800\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"8e87ff12-dfc2-472a-bbf1-5f2e891e864c\",\n" +
                "         \"groupId\":3,\n" +
                "         \"sequence\":5,\n" +
                "         \"command\":\"84F2200210F25397DCFB728E25FBEE52E748A116A800\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"validUntil\":\"2020-12-11T21:22:58.691Z\",\n" +
                "   \"apduPackageUrl\":\"http://localhost:9103/transportservice/v1/apdupackages/baff08fb-0b73-5019-8877-7c490a43dc64\"\n" +
                "}";
        return apduJson;

    }

}
