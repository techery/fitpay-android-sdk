package com.fitpay.android.paymentdevice.impl.mock;

import android.annotation.SuppressLint;
import android.util.Log;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.card.TopOfWallet;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.CreditCardCommit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.impl.PaymentDeviceConnector;
import com.fitpay.android.utils.Hex;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;

/**
 * Created by tgs on 5/3/16.
 */
@SuppressLint("SupportAnnotationUsage")
public class MockPaymentDeviceConnector extends PaymentDeviceConnector {

    private final String TAG = MockPaymentDeviceConnector.class.getSimpleName();

    public static final String CONFIG_DEFAULT_DELAY_TIME = "DEFAULT_DELAY_TIME";
    public static final String CONFIG_CONNECTING_RESPONSE_TIME = "CONNECTING_RESPONSE_TIME";
    public static final String CONFIG_CONNECTED_RESPONSE_TIME = "CONNECTED_RESPONSE_TIME";
    public static final String CONFIG_DISCONNECTING_RESPONSE_TIME = "DISCONNECTING_RESPONSE_TIME";
    public static final String CONFIG_DISCONNECTED_RESPONSE_TIME = "DISCONNECTED_RESPONSE_TIME";
    private static final int DEFAULT_DELAY = 2000;

    private int delay = DEFAULT_DELAY;

    private Properties config;

    /*
     * credit cards is a mock of a payment device that has local storage and / or display
     * For simplicity the whole card is stored typically the information transaferred and stored
     * would be much less.  In the case of Pebble Pagare, it is masked card number and card art.
     */
    private Map<String, CreditCardCommit> creditCards;

    private SyncCompleteListener syncCompleteListener = new SyncCompleteListener();

    public MockPaymentDeviceConnector() {
        state = States.INITIALIZED;

        // configure commit handlers
        addCommitHandler(CommitTypes.CREDITCARD_CREATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_ACTIVATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_DEACTIVATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_REACTIVATED, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.RESET_DEFAULT_CREDITCARD, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.SET_DEFAULT_CREDITCARD, new MockWalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_DELETED, new MockWalletDeleteCommitHandler());
    }

    @Override
    public void init(Properties props) {
        if (null == config) {
            config = props;
        } else {
            config.putAll(props);
        }
        if (config.contains(CONFIG_DEFAULT_DELAY_TIME)) {
            delay = getIntValue(config.getProperty(CONFIG_DEFAULT_DELAY_TIME));
        }
    }

    @Override
    public void close() {
        Log.d(TAG, "close not implemented");
    }

    @Override
    public void connect() {
        Log.d(TAG, "payment device connect requested.   current state: " + getState());

        NotificationManager.getInstance().addListenerToCurrentThread(syncCompleteListener);

        if (getState() == States.CONNECTED) {
            return;
        }

        setStateWithDelay(CONFIG_CONNECTING_RESPONSE_TIME, States.CONNECTING)
                .flatMap(x -> setStateWithDelay(CONFIG_CONNECTED_RESPONSE_TIME, States.CONNECTED))
                .subscribe(
                        x -> Log.d(TAG, "connect success"),
                        throwable -> Log.d(TAG, "connect error:" + throwable.toString()),
                        () -> {
                            Log.d(TAG, "connect complete");
                            readDeviceInfo();
                        });
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "payment device disconnect requested.  current state: " + getState());

        if (null != syncCompleteListener) {
            NotificationManager.getInstance().removeListener(syncCompleteListener);
        }

        setStateWithDelay(CONFIG_DISCONNECTING_RESPONSE_TIME, States.DISCONNECTING)
                .flatMap(x -> setStateWithDelay(CONFIG_DISCONNECTED_RESPONSE_TIME, States.DISCONNECTED))
                .subscribe(
                        x -> Log.d(TAG, "disconnect success"),
                        throwable -> Log.d(TAG, "disconnect error:" + throwable.toString()),
                        () -> Log.d(TAG, "disconnect complete"));
    }

    @Override
    public void readDeviceInfo() {
        Log.d(TAG, "payment device readDeviceInfo requested");

        getDelayObservable()
                .map(o -> loadDefaultDevice())
                .subscribe(deviceInfo -> {
                    Log.d(TAG, "device info has been read.  device: " + deviceInfo);
                    RxBus.getInstance().post(deviceInfo);
                }, throwable -> Log.d(TAG, "read device info error:" + throwable.toString()));
    }

    @Override
    public void readNFCState() {
    }

    @Override
    public void setNFCState(@NFC.Action byte state) {
    }

    @Override
    public void sendNotification(byte[] data) {
    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {
    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {
        ApduExecutionResult apduExecutionResult = new ApduExecutionResult(apduPackage.getPackageId());
        apduExecutionResult.setExecutedTsEpoch(System.currentTimeMillis());

        getDelayObservable()
                .map(x -> true)
                .subscribe(getApduObserver(apduPackage, apduExecutionResult, 0));
    }

    @Override
    public void executeTopOfWallet(List<TopOfWallet> towPackage) {
        getDelayObservable()
                .subscribe(
                        x -> Log.d(TAG, "execute TOW success"),
                        throwable -> Log.d(TAG, "execute TOW error" + throwable.toString()),
                        () -> Log.d(TAG, "execute TOW complete"));
    }

    private int getIntValue(String value) {
        int intValue = delay;
        try {
            intValue = Integer.parseInt(value);
        } catch (Exception ex) {
            Log.d(TAG, "could not convert string to int: " + value);
        }
        return intValue;
    }

    private int getTimeValueFromConfig(String key) {
        if (null == config || null == config.getProperty(key)) {
            return delay;
        }
        return getIntValue(config.getProperty(key));
    }

    private Device loadDefaultDevice() {
        return new Device.Builder()
                .setDeviceType(DeviceTypes.WATCH)
                .setManufacturerName("Fitpay")
                .setDeviceName("PSPS")
                .setSerialNumber("6e660ad5-7b06-4dec-ba4c-53b72a770f86")//(UUID.randomUUID().toString())
                .setModelNumber("FB404")
                .setHardwareRevision("1.0.0.0")
                .setFirmwareRevision("1030.6408.1309.0001")
                .setSoftwareRevision("2.0.242009.6")
                .setSystemId("0x123456FFFE9ABCDE")
                .setOSName("ANDROID")
                .setLicenseKey("6b413f37-90a9-47ed-962d-80e6a3528036")
                .setBdAddress("96c37b2c-7848-406f-97b7-16d995c5f5a3")//(UUID.randomUUID().toString())
                .setSecureElementId("4691b9a0-45d6-4268-8b78-8708c5163019")//(UUID.randomUUID().toString())
                .build();
    }

    private Observable<Object> getDelayObservable() {
        return getDelayObservable(delay);
    }

    private Observable<Object> getDelayObservable(String timeValue) {
        return getDelayObservable(getTimeValueFromConfig(timeValue));
    }

    private Observable<Object> getDelayObservable(int responseDelay) {
        return Observable.timer(responseDelay, TimeUnit.MILLISECONDS)
                .compose(RxBus.applySchedulersExecutorThread());
    }

    private Observable<Object> setStateWithDelay(final String timeValue, final @Connection.State int targetState) {
        return getDelayObservable(getTimeValueFromConfig(timeValue))
                .map(x -> {
                    setState(targetState);
                    return Observable.just(null);
                }).compose(RxBus.applySchedulersExecutorThread());
    }

    private Observer<Boolean> getApduObserver(final ApduPackage apduPackage, final ApduExecutionResult apduExecutionResult, int apduCommandNumber) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "Get response for apduCommand: " + apduCommandNumber);
                ApduCommand apduCommand = apduPackage.getApduCommands().get(apduCommandNumber);
                ApduCommandResult apduCommandResult = getMockResultForApduCommand(apduCommand);
                apduExecutionResult.addResponse(apduCommandResult);
                Log.d(TAG, "apduExecutionResult: " + apduExecutionResult);

                if (!ResponseState.PROCESSED.equals(apduExecutionResult.getState())) {
                    Log.d(TAG, "apduExecutionResult: " + apduExecutionResult);
                    int duration = (int) ((System.currentTimeMillis() - apduExecutionResult.getExecutedTsEpoch()) / 1000);
                    apduExecutionResult.setExecutedDuration(duration);
                    Log.d(TAG, "apdu processing is complete.  Result: " + new Gson().toJson(apduExecutionResult));
                    RxBus.getInstance().post(apduExecutionResult);
                } else if (apduCommandNumber + 1 < apduPackage.getApduCommands().size()) {
                    getDelayObservable(100)
                            .map(x -> true)
                            .subscribe(getApduObserver(apduPackage, apduExecutionResult, apduCommandNumber + 1));
                } else {
                    Log.d(TAG, "apduExecutionResult: " + apduExecutionResult);
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
        String responseData = "9000";

        byte[] request = apduCommand.getCommand();
        // should we simulate an error?  if the first byte is 0x99 or 0x98, then the
        // next two represent the simulated error
        if (request[0] == (byte) 0x99) {
            responseData = Hex.bytesToHexString(new byte[]{request[1], request[2]});
        }

        if (request[0] == (byte) 0x98) {
            byte[] val = new byte[request.length + 2];
            System.arraycopy(request, 0, val, 0, request.length);
            System.arraycopy(new byte[]{request[1], request[2]}, 0, val, request.length, 2);
            responseData = Hex.bytesToHexString(val);
        }

        String responseCode = responseData.substring(responseData.length() - 4);

        ApduCommandResult result = new ApduCommandResult.Builder()
                .setCommandId(apduCommand.getCommandId())
                .setResponseData(responseData)
                .setResponseCode(responseCode)
                .build();
        return result;
    }


    public void updateWallet(CreditCardCommit card) {
        if (getWallet().containsKey(card.getCreditCardId())) {
            Log.d(TAG, "Updating credit card in mock wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        } else {
            Log.d(TAG, "Adding credit card to mock wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        }
        getWallet().put(card.getCreditCardId(), card);
    }

    public void removeCardFromWallet(String creditCardId) {
        Log.d(TAG, "Credit card updated in mock wallet.  remove card: : " + creditCardId);
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

    private class SyncCompleteListener extends Listener {

        private SyncCompleteListener() {
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        public void onSyncStateChanged(Sync syncEvent) {
            Log.d(TAG, "received on sync state changed event: " + syncEvent);

            switch (syncEvent.getState()) {
                case States.COMPLETED:
                    getTopOfWalletData();
                    break;

                case States.COMPLETED_NO_UPDATES:
                    break;
            }
        }
    }

    private class MockWalletDeleteCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            if (!(payload instanceof CreditCardCommit)) {
                Log.e(TAG, "Mock Wallet received a commit to process that was not a credit card commit.  Commit: " + commit);
                RxBus.getInstance().post(new CommitFailed.Builder()
                        .commit(commit)
                        .errorCode(999)
                        .errorMessage("Commit does not contain a credit card")
                        .build());

                return;
            }
            // process with a delay to mock device response time
            getDelayObservable(100).subscribe(
                    o -> Log.d(TAG, "processCommit " + commit.getCommitType()),
                    throwable -> Log.d(TAG, String.format("processCommit %s error:%s", commit.getCommitType(), throwable.toString())),
                    () -> {
                        CreditCardCommit card = (CreditCardCommit) commit.getPayload();
                        Log.d(TAG, "Mock wallet has been updated. Card removed: " + card.getCreditCardId());
                        removeCardFromWallet(card.getCreditCardId());
                        RxBus.getInstance().post(new CommitSuccess(commit));
                    }
            );
        }
    }

    private class MockWalletUpdateCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            if (!(payload instanceof CreditCardCommit)) {
                Log.e(TAG, "Mock Wallet received a commit to process that was not a credit card commit.  Commit: " + commit);
                RxBus.getInstance().post(new CommitFailed.Builder()
                        .commit(commit)
                        .errorCode(999)
                        .errorMessage("Commit does not contain a credit card")
                        .build());
                return;
            }

            // process with a delay to mock device response time
            getDelayObservable(100).subscribe(
                    o -> Log.d(TAG, "processCommit " + commit.getCommitType()),
                    throwable -> Log.d(TAG, String.format("processCommit %s error:%s", commit.getCommitType(), throwable.toString())),
                    () -> {
                        CreditCardCommit card = (CreditCardCommit) commit.getPayload();
                        Log.d(TAG, "Mock wallet has been updated. Card updated: " + card.getCreditCardId());
                        updateWallet(card);
                        RxBus.getInstance().post(new CommitSuccess(commit));
                    }
            );
        }
    }
}
