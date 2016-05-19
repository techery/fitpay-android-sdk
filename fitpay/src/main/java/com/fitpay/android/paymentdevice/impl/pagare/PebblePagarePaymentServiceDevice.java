package com.fitpay.android.paymentdevice.impl.pagare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.CreditCardCommit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.callbacks.IListeners;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.impl.pagare.model.WalletEntry;
import com.fitpay.android.paymentdevice.model.PaymentDeviceService;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tgs on 5/16/16.
 */
public class PebblePagarePaymentServiceDevice extends PaymentDeviceService {

    private final static String TAG = PebblePagarePaymentServiceDevice.class.getSimpleName();

    public static final String EXTRA_PEBBLE_APP_UUID = "PEBBLE_APP_UUID";

    private UUID pebbleAppUuid;

    // container for device information as returned from read operation.   Will not contain contain links
    private Device device;

    // for mock response delay
    private final int delay = 3000;
    private final Random random = new Random();

    private Map<String, WalletEntry> wallet;
    private SyncCompleteListener syncCompleteListener;


    public PebblePagarePaymentServiceDevice() {
        state = States.INITIALIZED;
        loadDefaultDevice();

        // configure commit handlers
        addCommitHandler(CommitTypes.CREDITCARD_CREATED, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_ACTIVATED, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_DEACTIVATED, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.RESET_DEFAULT_CREDITCARD, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.SET_DEFAULT_CREDITCARD, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_DELETED, new WalletUpdateCommitHandler());

    }

    @Override
    public void init(Properties props) {
        if (null != props.getProperty(EXTRA_PEBBLE_APP_UUID)) {
            // this will throw an IllegalArgumentException if not a valid UUID string
            // - that is OK since can not connect without a valid value
            pebbleAppUuid = UUID.fromString(props.getProperty(EXTRA_PEBBLE_APP_UUID));
        }
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void connect() {
        boolean isConnected = PebbleKit.isWatchConnected(mContext);
        if (isConnected) {
            setState(States.CONNECTED);
        } else {
            setState(States.DISCONNECTED);
            //TODO need to fire some kind of event to inform client that connect failed
        }
        PebbleKit.registerPebbleConnectedReceiver(this.mContext, connectionBroadcastReceiver);
        PebbleKit.registerPebbleDisconnectedReceiver(this.mContext, connectionBroadcastReceiver);
        syncCompleteListener = new SyncCompleteListener();
        NotificationManager.getInstance().addListener(syncCompleteListener);

        // use this entry point to initialize wallet
        syncWalletState();
    }

    private BroadcastReceiver connectionBroadcastReceiver = new ConnectionBroadcastReceiver();

    @Override
    public void disconnect() {
        if (null != syncCompleteListener) {
            NotificationManager.getInstance().removeListener(syncCompleteListener);
            syncCompleteListener = null;
        }
    }

    @Override
    public void readDeviceInfo() {
        // Is the watch connected?
        boolean isConnected = PebbleKit.isWatchConnected(mContext);
        if (!isConnected) {
            //TODO need to fire some kind of event to inform client that connect and hence readDeviceInfo failed
            return;
        }

        Subscription subscription = getReadPebbleDeviceInfoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getPebbleDeviceInfoObserver());

        // Mock device read

        Subscription deviceReadSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getDeviceInfoObserver(device));

    }

    @Override
    public void readNFCState() {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void setNFCState(@NFC.Action byte state) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void sendNotification(byte[] data) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    private Observable<PebbleDeviceInfo> getReadPebbleDeviceInfoObservable() {
        return Observable.just(true).map(new Func1<Boolean, PebbleDeviceInfo>() {
            @Override
            public PebbleDeviceInfo call(Boolean aBoolean) {
                PebbleKit.FirmwareVersionInfo info = PebbleKit.getWatchFWVersion(mContext);
                boolean appMessageSupported = PebbleKit.areAppMessagesSupported(mContext);
                PebbleDeviceInfo pebble = new PebbleDeviceInfo.Builder()
                        .setFirmwareVersion(info.getMajor() + "." + info.getMinor() + "." + info.getPoint())
                        .setAppMessageSupported(appMessageSupported)
                        .build();
                return pebble;
            }
        });
    }


    private Observer<PebbleDeviceInfo> getPebbleDeviceInfoObserver() {

        return new Observer<PebbleDeviceInfo>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "pebble deviceInfo observer completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "pebble deviceInfo observer error: " + e.getMessage());
            }

            @Override
            public void onNext(PebbleDeviceInfo deviceInfo) {
                Log.d(TAG, "pebble device info has been read.  device: " + deviceInfo);
                if (null != deviceInfo) {
                    RxBus.getInstance().post(deviceInfo);
                } else {
                    Log.e(TAG, "pebble read device info returned null. This is a application defect");
                }
            }
        };
    }

    // Mock device info methods
    protected void loadDefaultDevice() {
        device = new Device.Builder()
                .setDeviceType(DeviceTypes.WATCH)
                .setManufacturerName("Fitpay")
                .setDeviceName("Pagare Smart Strap")
                .setSerialNumber("074DC456B5")
                .setModelNumber("Pagare One")
                .setHardwareRevision("1.0.0.0")
                .setFirmwareRevision("1030.6408.1309.0001")
                //.setSoftwareRevision("2.0.242009.6")
                .setSystemId("0x123456FFFE9ABCDE")
                .setOSName("ANDROID")
                .setLicenseKey("6b413f37-90a9-47ed-962d-80e6a3528036")
                //.setBdAddress("977214bf-d038-4077-bdf8-226b17d5958d")
                .setSecureElementId("8765b2c7-74c5-43e5-b224-39992060161b")
                .build();
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

    private class ConnectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Pebble connection state has changed.  Received intent:  " + intent);
            String action = intent.getAction();
            if (action.equalsIgnoreCase(Constants.INTENT_PEBBLE_CONNECTED)) {
                setState(States.CONNECTED);
            } else if (action.equalsIgnoreCase(Constants.INTENT_PEBBLE_DISCONNECTED)) {
                setState(States.DISCONNECTED);
            }
        }
    }

    private void rebuildWallet(String lastCommitId) {
        if (null == lastCommitId) {
            wallet = new HashMap<>();
            return;
        }
        //TODO need to get commmits but to do so need a reference to Device

    }

    private synchronized void updateWallet(CreditCardCommit card) {
        if (getWallet().containsKey(card.getCreditCardId())) {
            Log.i(TAG, "Updating credit card in mock wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        } else {
            Log.i(TAG, "Adding credit card to mock wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        }
        if (null == card.getPan()) {
            Log.w(TAG, "commit for credit card update does not contain pan: " + card.getCreditCardId());
        }
        WalletEntry walletEntry = getWallet().get(card.getCreditCardId());
        WalletEntry walletUpdate = getWalletEntry(card);
        Log.d(TAG, "wallet update for card: " + card.getCreditCardId() + ": " + walletEntry);
        if (null == walletEntry) {
            wallet.put(card.getCreditCardId(), walletUpdate);
        } else {
            if (null != walletUpdate.getPan()) {
                walletEntry.setPan(walletUpdate.getPan());
            }
            if (walletUpdate.getExpMonth() > 0) {
                walletEntry.setExpMonth(walletUpdate.getExpMonth());
            }
            if (walletUpdate.getExpYear() > 0) {
                walletEntry.setExpYear(walletUpdate.getExpYear());
            }
            if (null != walletUpdate.getCardType()) {
                walletEntry.setCardType(walletUpdate.getCardType());
            }
            walletEntry.setActive(walletUpdate.isActive());
            walletEntry.setDefault(walletUpdate.isDefault());
            walletEntry.setMostRecentTouch(walletUpdate.isMostRecentTouch());

            if (walletUpdate.isMostRecentTouch()) {
                for (String key: getWallet().keySet()) {
                    if (!key.equals(card.getCreditCardId())) {
                        getWallet().get(key).setMostRecentTouch(false);
                    }
                }
            }
        }
        Log.d(TAG, "current wallet entry for card: " + card.getCreditCardId() + ": " + walletEntry);
        Log.d(TAG, "current wallet contents: " + wallet);
    }

    private void removeCardFromWallet(String creditCardId) {
        Log.i(TAG, "Credit card updated in mock wallet.  remove card: : " + creditCardId);
        getWallet().remove(creditCardId);
    }

    public Map<String, WalletEntry> getWallet() {
        if (null == wallet) {
            //TODO should initialize wallet from local storage and then apply incremental changes
            // if no copy in local storage then would need to initialize from beginning of life to lastCommitId to catchup
            // For now just have delta changes
            wallet = new HashMap<>();
        }
        return wallet;
    }

    private WalletEntry getWalletEntry(CreditCardCommit card) {
        return new WalletEntry.Builder()
                .setPan(card.getPan())
                .setCardType(card.getCardType())
                .setDefault(card.isDefault())
                .setActive("ACTIVE".equals(card.getState()))
                .setMostRecentTouch(true)
                .setExpMonth(card.getExpMonth())
                .setExpYear(card.getExpYear())
                .build();
    }


    private class WalletUpdateCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            Log.d(TAG, "commit payload: " + payload);
            if (!(payload instanceof CreditCardCommit)) {
                Log.e(TAG, "Wallet received a commit to process that was not a credit card.  Commit: " + commit);
                RxBus.getInstance().post(new CommitFailed(commit.getCommitId()));
                return;
            }
            if (CommitTypes.CREDITCARD_DELETED.equals(commit.getCommitType())) {
                removeCardFromWallet(((CreditCardCommit) payload).getCreditCardId());
            } else {
                updateWallet((CreditCardCommit) payload);
            }
            DevicePreferenceData data = DevicePreferenceData.loadFromPreferences(mContext, device.getDeviceIdentifier());
            data.putAdditionalValue("wallet", new Gson().toJson(wallet));
            DevicePreferenceData.storePreferences(mContext, data);
            RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
        }
    }

    private void syncWalletState() {
        //TODO should this be implemented?   if last commit id is not null but wallet not defined then it should be rebuilt
    }

    private void sendWalletToPebble() {
        Log.d(TAG, "sending wallet update to pebble app");
        PebbleKit.startAppOnPebble(mContext, this.pebbleAppUuid);
        PebbleDictionary dict = getPebbleUpdateWalletMessage();
        PebbleKit.sendDataToPebble(mContext, this.pebbleAppUuid, dict);
    }

    private PebbleDictionary getPebbleUpdateWalletMessage() {
        PebbleDictionary dict = new PebbleDictionary();
        int msgId = 0x2000;
        for (WalletEntry entry: wallet.values()) {
            dict.addString(msgId + 0x00, entry.getPan().substring(entry.getPan().length() - 4));
            dict.addString(msgId + 0x01, "" + entry.getExpYear());
            dict.addString(msgId + 0x02, entry.getExpMonth() < 10 ? "0" + entry.getExpMonth() : "" + entry.getExpMonth());
            dict.addString(msgId + 0x03, entry.getCardType());
            dict.addInt32(msgId + 0x04, entry.isActive() ? 1 : 0);
            dict.addInt32(msgId + 0x05, entry.isDefault() ? 1 : 0);
            dict.addString(msgId + 0x06, "");   //TODO implement passing mostRecentTouch
            msgId += 0x0010;
        }
        return dict;
    }


    private class SyncCompleteListener extends Listener implements IListeners.SyncListener {

        private SyncCompleteListener(){
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        @Override
        public void onSyncStateChanged(Sync syncEvent) {
            Log.d(TAG, "received on sync state changed event: " + syncEvent);
            if (syncEvent.getState() == States.COMPLETED) {
                sendWalletToPebble();
                NotificationManager.getInstance().removeListener(this);
            }
        }

        @Override
        public void onCommitFailed(CommitFailed commitFailed) {
            // do nothing
        }

        @Override
        public void onNonApduCommit(Commit commit) {
            // do nothing
        }

        @Override
        public void onCommitSuccess(CommitSuccess commitSuccess) {
            // do nothing
        }
    }



}

