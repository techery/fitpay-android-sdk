package com.fitpay.android.paymentdevice.impl.mock;

import android.util.Log;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.CreditCardCommit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.model.PaymentDeviceConnector;
import com.fitpay.android.utils.RxBus;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tgs on 5/3/16.
 */
public class MockPaymentDeviceConnector extends PaymentDeviceConnector {

    private final String TAG = MockPaymentDeviceConnector.class.getSimpleName();

    private Device device;
    private final Random random = new Random();
    private final int delay = 3000;

    /*
     * credit cards is a mock of a payment device that has local storage and / or display
     * For simplicity the whole card is stored typically the information transaferred and stored
     * would be much less.  In the case of Pebble Pagare, it is masked card number and card art.
     */
    private Map<String, CreditCardCommit> creditCards;

    Subscription connectionSubscription;
    Subscription deviceReadSubscription;
    Subscription apduSubscription;

    private ApduExecutionResult apduExecutionResult;

    public MockPaymentDeviceConnector() {
        state = States.INITIALIZED;
        loadDefaultDevice();

        // configure commit handlers
        addCommitHandler(CommitTypes.CREDITCARD_CREATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_ACTIVATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_DEACTIVATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_REACTIVATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.RESET_DEFAULT_CREDITCARD, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.SET_DEFAULT_CREDITCARD, new MockWalletUpdateCommitHandler());

        addCommitHandler(CommitTypes.CREDITCARD_DELETED, new MockWalletDeleteCommitHandler());
    }

    protected void loadDefaultDevice() {
        device = new Device.Builder()
                .setDeviceType(DeviceTypes.WATCH)
                .setManufacturerName("Fitpay")
                .setDeviceName("PSPS")
                .setSerialNumber("074DCC022E14")
                .setModelNumber("FB404")
                .setHardwareRevision("1.0.0.0")
                .setFirmwareRevision("1030.6408.1309.0001")
                .setSoftwareRevision("2.0.242009.6")
                .setSystemId("0x123456FFFE9ABCDE")
                .setOSName("ANDROID")
                .setLicenseKey("6b413f37-90a9-47ed-962d-80e6a3528036")
                .setBdAddress("977214bf-d038-4077-bdf8-226b17d5958d")
                .setSecureElementId("8615b2c7-74c5-43e5-b224-38882060161b")
                .build();
    }

    @Override
    public void close() {
        Log.d(TAG, "close not implemented");
    }

    @Override
    public void connect() {

        Log.d(TAG, "payment device connect requested.   current state: " + getState());

        if (getState() == States.CONNECTED) {
            return;
        }

        connectionSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getConnectionObserver(States.CONNECTING));
    }

    private Observable<Boolean> getAsyncSimulatingObservable() {
        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                delay(delay);
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getConnectionObserver(@Connection.State int targetState) {

        return new Observer<Boolean>() {

            private final int newState = targetState;

            @Override
            public void onCompleted() {
                Log.d(TAG, "connection changed state.  new state: " + newState);
                setState(newState);
                if (newState == States.CONNECTING) {
                    connectionSubscription = getAsyncSimulatingObservable()//
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe(getConnectionObserver(States.CONNECTED));
                } else if (newState == States.DISCONNECTING) {
                    connectionSubscription = getAsyncSimulatingObservable()//
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe(getConnectionObserver(States.DISCONNECTED));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "connection observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "connection observer onNext: " + bool);
            }
        };
    }


    private Observer<Boolean> getDeviceInfoObserver(final Device deviceInfo) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "device info has been read.  device: " + deviceInfo);
                if (null != deviceInfo) {
                    RxBus.getInstance().post(deviceInfo);
                } else {
                    Log.e(TAG, "read device info returned null. This is a application defect");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "deviceInfo observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "deviceInfo observer onNext: " + bool);
            }
        };
    }

    protected void delay(long delayInterval) {

        try {
            Thread.sleep(random.nextInt((int)delayInterval));
        } catch (InterruptedException e) {
            // carry on
        }
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "payment device disconnect requested.  current state: " + getState());

        connectionSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getConnectionObserver(States.DISCONNECTING));
    }

    @Override
    public void readDeviceInfo() {

        Log.d(TAG, "payment device readDeviceInfo requested");

        deviceReadSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getDeviceInfoObserver(device));
    }

    @Override
    public void readNFCState() {

    }

    @Override
    public void setNFCState(@NFC.Action byte state) {

    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {

        ApduExecutionResult apduExecutionResult = new ApduExecutionResult(apduPackage.getPackageId());
        apduExecutionResult.setExecutedTsEpoch(System.currentTimeMillis());

        apduSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getApduObserver(apduPackage, apduExecutionResult, 0));

    }

    private Observer<Boolean> getApduObserver(final ApduPackage apduPackage, final ApduExecutionResult apduExecutionResult, int apduCommandNumber) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "Get response for apduCommand: " + apduCommandNumber);
                ApduCommand apduCommand = apduPackage.getApduCommands().get(apduCommandNumber);
                ApduCommandResult apduCommandResult = getMockResultForApduCommand(apduCommand);
                apduExecutionResult.addResponse(apduCommandResult);

                if (apduCommandNumber + 1 < apduPackage.getApduCommands().size()) {
                    apduSubscription = getAsyncSimulatingObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe(getApduObserver(apduPackage, apduExecutionResult, apduCommandNumber + 1));

                } else {
                    apduExecutionResult.deriveState();
                    int duration = (int) ((System.currentTimeMillis() - apduExecutionResult.getExecutedTsEpoch()) / 1000);
                    apduExecutionResult.setExecutedDuration(duration);
                    Log.d(TAG, "apdu processing is complete.  Result: " + new Gson().toJson(apduExecutionResult));
                    RxBus.getInstance().post(apduExecutionResult);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "apdu observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "apdu observer onNext: " + bool);
            }
        };
    }

    private ApduCommandResult getMockResultForApduCommand(ApduCommand apduCommand) {
        ApduCommandResult result = new ApduCommandResult.Builder()
            .setCommandId(apduCommand.getCommandId())
                .setResponseData("9000")
                .setResponseCode("9000")
                .build();
        return result;
    }


    @Override
    public void sendNotification(byte[] data) {

    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {

    }

    public void updateWallet(CreditCardCommit card) {
        if (getWallet().containsKey(card.getCreditCardId())) {
            Log.i(TAG, "Updating credit card in mock wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        } else {
            Log.i(TAG, "Adding credit card to mock wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        }
        getWallet().put(card.getCreditCardId(), card);
    }

    public void removeCardFromWallet(String creditCardId) {
        Log.i(TAG, "Credit card updated in mock wallet.  remove card: : " + creditCardId);
        getWallet().remove(creditCardId);
    }

    public Map<String, CreditCardCommit> getWallet() {
        if (null == creditCards) {
            //TODO should initialize wallet from local storage and then apply incremental changes
            // if no copy in local storage then would need to initialize from beginning of life to lastCommitId to catchup
            // For now just have delta changes
            creditCards = new HashMap<>();
        }
        return creditCards;
    }


    private class MockWalletDeleteCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            Log.d(TAG, "commit payload: " + payload);
            if (!(payload instanceof CreditCardCommit)) {
                Log.e(TAG, "Mock Wallet received a commit to process that was not a credit card commit.  Commit: " + commit);
                RxBus.getInstance().post(new CommitFailed(commit.getCommitId()));
                return;
            }
            // process with a delay to mock device response time
            Subscription subscription = getAsyncSimulatingObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(getWalletObserver(commit));
        }

        private Observer<Boolean> getWalletObserver(final Commit commit) {

            return new Observer<Boolean>() {

                @Override
                public void onCompleted() {
                    CreditCardCommit card = (CreditCardCommit) commit.getPayload();
                    Log.d(TAG, "Mock wallet has been updated. Card removed: " + card.getCreditCardId());
                    removeCardFromWallet(card.getCreditCardId());
                    // signal commit processing is complete
                    RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "processCommit observer error: " + e.getMessage());
                }

                @Override
                public void onNext(Boolean bool) {
                    Log.d(TAG, "processCommit observer onNext: " + bool);
                }
            };
        }
    }

    private class MockWalletUpdateCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            Log.d(TAG, "commit payload: " + payload);
            if (!(payload instanceof CreditCardCommit)) {
                Log.e(TAG, "Mock Wallet received a commit to process that was not a credit card commit.  Commit: " + commit);
                RxBus.getInstance().post(new CommitFailed(commit.getCommitId()));
                return;
            }
            // process with a delay to mock device response time
            Subscription subscription = getAsyncSimulatingObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(getWalletObserver(commit));
        }

        private Observer<Boolean> getWalletObserver(final Commit commit) {

            return new Observer<Boolean>() {

                @Override
                public void onCompleted() {
                    CreditCardCommit card = (CreditCardCommit) commit.getPayload();
                    Log.d(TAG, "Mock wallet has been updated. Card updated: " + card.getCreditCardId());
                    updateWallet(card);
                    // signal commit processing is complete
                    RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "processCommit observer error: " + e.getMessage());
                }

                @Override
                public void onNext(Boolean bool) {
                    Log.d(TAG, "processCommit observer onNext: " + bool);
                }
            };
        }
    }


}
