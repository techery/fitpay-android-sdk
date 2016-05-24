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
import com.fitpay.android.paymentdevice.DeviceService;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    public static final String WALLET_KEY = "wallet";

    private static final int MESSAGE_ID_WALLET_UPDATE = 0x2000;

    private UUID pebbleAppUuid;

    // for mock response delay
    private final int delay = 3000;
    private final Random random = new Random();

    private String syncDeviceId;
    private Map<String, WalletEntry> wallet;
    private SyncCompleteListener syncCompleteListener;


    public PebblePagarePaymentServiceDevice() {
        state = States.INITIALIZED;

        // configure commit handlers
        addCommitHandler(CommitTypes.CREDITCARD_CREATED, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_ACTIVATED, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_DEACTIVATED, new WalletUpdateCommitHandler());
        addCommitHandler(CommitTypes.CREDITCARD_REACTIVATED, new WalletUpdateCommitHandler());
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
        if (null != props.getProperty(DeviceService.SYNC_PROPERTY_DEVICE_ID)) {
            syncDeviceId = props.getProperty(DeviceService.SYNC_PROPERTY_DEVICE_ID);
            DevicePreferenceData data = DevicePreferenceData.load(mContext, syncDeviceId);
            String serializedWallet = data.getAdditionalValue(WALLET_KEY);
            if (null != serializedWallet) {
                Type type = new TypeToken<Map<String, WalletEntry>>(){}.getType();
                wallet = new Gson().fromJson(serializedWallet, type);
            } else {
                wallet = new HashMap<>();
            }
        }
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void connect() {
        /*
         * In the case of Pebble, connected really means connected to the device and the Pagare App open
         */
        boolean isConnected = PebbleKit.isWatchConnected(mContext);
        if (isConnected) {
            setState(States.CONNECTED);
        } else {
            setState(States.DISCONNECTED);
            //TODO need to fire some kind of event to inform client that connect failed
        }
        PebbleKit.registerPebbleConnectedReceiver(this.mContext, connectionBroadcastReceiver);
        PebbleKit.registerPebbleDisconnectedReceiver(this.mContext, connectionBroadcastReceiver);

        PebbleKit.registerReceivedAckHandler(mContext,
                new PebbleKit.PebbleAckReceiver(pebbleAppUuid) {

                    @Override
                    public void receiveAck(Context context, int transactionId) {
                        Log.d(TAG, "received pebble ack: " + transactionId);
                        switch (transactionId) {
                            case MESSAGE_ID_WALLET_UPDATE: {
                                Log.d(TAG, "Wallet update message acknowledged");
                                break;
                            }
                            default: {
                                Log.d(TAG, "unhandled ACK for transaction: " + transactionId);
                            }
                        }
                    }

                });

        PebbleKit.registerReceivedNackHandler(mContext,
                new PebbleKit.PebbleNackReceiver(pebbleAppUuid) {

                    @Override
                    public void receiveNack(Context context, int transactionId) {
                        Log.d(TAG, "received pebble nack: " + transactionId);
                        switch (transactionId) {
                            case MESSAGE_ID_WALLET_UPDATE: {
                                Log.d(TAG, "Wallet update message nacked");
                                break;
                            }
                            default: {
                                Log.d(TAG, "unhandled NACK for transaction: " + transactionId);
                            }
                        }
                    }

                });

        syncCompleteListener = new SyncCompleteListener();
        NotificationManager.getInstance().addListener(syncCompleteListener);

        // use this entry point to initialize wallet
        syncWalletState();
    }

    private BroadcastReceiver connectionBroadcastReceiver = new PebbleConnectionBroadcastReceiver();

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
                .subscribe(getDeviceInfoObserver(getMockDevice()));

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

    @Override
    public void syncInit() {
        super.syncInit();
        PebbleKit.startAppOnPebble(mContext, pebbleAppUuid);
    }

    @Override
    public void syncComplete() {
        //TODO need to identify how DeviceService can invoke this without using syncCompleteListener
        super.syncComplete();
        NotificationManager.getInstance().removeListener(syncCompleteListener);
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
    protected Device getMockDevice() {
        Device mockDevice = new Device.Builder()
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
        return mockDevice;
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


    private void rebuildWallet(String lastCommitId) {
        if (null == lastCommitId) {
            wallet = new HashMap<>();
            return;
        }
        //TODO need to get commmits but to do so need a reference to Device

    }

    private synchronized void updateWallet(CreditCardCommit card) {
        if (getWallet().containsKey(card.getCreditCardId())) {
            Log.i(TAG, "Updating credit card in wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
        } else {
            Log.i(TAG, "Adding credit card to wallet.  Id: " + card.getCreditCardId() + ", pan: " + card.getPan());
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
        Log.i(TAG, "Credit card updated in wallet.  remove card: : " + creditCardId);
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
            Log.d(TAG, "updating stored wallet");
            DevicePreferenceData data = DevicePreferenceData.load(mContext, syncDeviceId);
            data.putAdditionalValue(WALLET_KEY, new Gson().toJson(wallet));
            DevicePreferenceData.store(mContext, data);
            RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
        }
    }

    private void syncWalletState() {
        //TODO should this be implemented?   if last commit id is not null but wallet not defined then it should be rebuilt
    }

    private void sendWalletToPebble() {
        PebbleDictionary dict = getPebbleUpdateWalletMessage();
        if (null == dict || dict.size() == 0) {
            Log.d(TAG, "wallet is empty");
            dict = getPebbleEmptyWalletMessage();
        }
        dict.addString(0x2FFF, "Wallet updates were successful");
        Log.d(TAG, "sending wallet update to pebble app.  transactionId: " + MESSAGE_ID_WALLET_UPDATE + ", size: " + dict.size());
        PebbleKit.sendDataToPebbleWithTransactionId(mContext, this.pebbleAppUuid, dict, MESSAGE_ID_WALLET_UPDATE);
    }

    private PebbleDictionary getPebbleUpdateWalletMessage() {
        if (null == wallet || wallet.size() == 0) {
            return null;
        }
        PebbleDictionary dict = new PebbleDictionary();
        int msgId = MESSAGE_ID_WALLET_UPDATE;
        for (WalletEntry entry: wallet.values()) {
            dict.addString(msgId + 0x00, entry.getPan().substring(entry.getPan().length() - 4));
            dict.addString(msgId + 0x01, "" + entry.getExpYear());
            dict.addString(msgId + 0x02, entry.getExpMonth() < 10 ? "0" + entry.getExpMonth() : "" + entry.getExpMonth());
            dict.addString(msgId + 0x03, entry.getCardType());
            dict.addInt32(msgId + 0x04, entry.isActive() ? 1 : 0);
            dict.addInt32(msgId + 0x05, entry.isDefault() ? 1 : 0);
            dict.addInt32(msgId + 0x06, entry.isMostRecentTouch() ? 1 : 0);
            msgId += 0x0010;
        }
        return dict;
    }

    private PebbleDictionary getPebbleEmptyWalletMessage() {
        PebbleDictionary dict = new PebbleDictionary();
        dict.addString(8191, "");
        return dict;
    }


    private class SyncCompleteListener extends Listener  {

        private SyncCompleteListener(){
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        public void onSyncStateChanged(Sync syncEvent) {
            Log.d(TAG, "received on sync state changed event: " + syncEvent);
            if (syncEvent.getState() == States.COMPLETED) {
                sendWalletToPebble();
                // At this point, we want to unregister this listener
                // but a listener can not unregister itself without throwing a ConcurrentModificationException
            }
        }
    }


    private class PebbleConnectionBroadcastReceiver extends BroadcastReceiver {

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




}

