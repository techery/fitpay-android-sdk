package com.fitpay.android.paymentdevice.impl.pagare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.device.CreditCardCommit;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.DeviceOperationException;
import com.fitpay.android.paymentdevice.DeviceService;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.events.PaymentDeviceOperationFailed;
import com.fitpay.android.paymentdevice.impl.PaymentDeviceConnector;
import com.fitpay.android.paymentdevice.impl.pagare.model.WalletEntry;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.Hex;
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
import java.util.UUID;

/**
 * Created by tgs on 5/16/16.
 */
public class PebblePagarePaymentDeviceConnector extends PaymentDeviceConnector {

    private final static String TAG = PebblePagarePaymentDeviceConnector.class.getSimpleName();

    public static final String EXTRA_PEBBLE_APP_UUID = "PEBBLE_APP_UUID";
    public static final String WALLET_KEY = "wallet";

    private static final int MESSAGE_ID_WALLET_UPDATE = 0x2000;
    private static final int MESSAGE_ID_DEVICE_INFO_DEVICE_TYPE = 8190;
    private static final int MESSAGE_ID_DEVICE_INFO_MANUFACTURER_NAME = 8191;
    private static final int MESSAGE_ID_DEVICE_INFO_DEVICE_NAME       = 8192;
    private static final int MESSAGE_ID_DEVICE_INFO_SERIAL_NUMBER     = 8193;
    private static final int MESSAGE_ID_DEVICE_INFO_MODEL_NUMBER      = 8194;
    private static final int MESSAGE_ID_DEVICE_INFO_HARDWARE_REVISION = 8195;
    private static final int MESSAGE_ID_DEVICE_INFO_FIRMWARE_REVISION = 8196;
    private static final int MESSAGE_ID_DEVICE_INFO_SOFTWARE_REVISION = 8197;
    private static final int MESSAGE_ID_DEVICE_INFO_SYSTEM_ID         = 8198;
    private static final int MESSAGE_ID_DEVICE_INFO_OS_NAME           = 8199;
    private static final int MESSAGE_ID_DEVICE_INFO_LICENSE_KEY       = 8200;
    private static final int MESSAGE_ID_DEVICE_INFO_BD_ADDRESS        = 8201;
    private static final int MESSAGE_ID_DEVICE_INFO_SECURE_ELEMENT_ID  = 8202;

    private static final int MESSAGE_ID_APDU_COMMAND  = 36864;
    private static final int MESSAGE_ID_APDU_SEQUENCE_NUMBER = 36865;

    private static final int MESSAGE_ID_APDU_COMMAND_RESULT  = 36864;

    private UUID pebbleAppUuid;

    private final int connectDelay = 2000;

    private String syncDeviceId;
    private Map<String, WalletEntry> wallet;
    private SyncCompleteListener syncCompleteListener;


    public PebblePagarePaymentDeviceConnector() {
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
        PebbleKit.registerPebbleConnectedReceiver(this.mContext, connectionBroadcastReceiver);
        PebbleKit.registerPebbleDisconnectedReceiver(this.mContext, connectionBroadcastReceiver);

        /*
         * In the case of Pebble, connected really means connected to the device and the Pagare App open
         */
        boolean isConnected = PebbleKit.isWatchConnected(mContext);
        if (isConnected) {
            setState(States.CONNECTED);
            Log.d(TAG, "start watch pagare app");
            PebbleKit.startAppOnPebble(mContext, pebbleAppUuid);
            delay(connectDelay);
        } else {
            setState(States.DISCONNECTED);
            //TODO need to fire some kind of event to inform client that connect failed
        }

        syncCompleteListener = new SyncCompleteListener();
        NotificationManager.getInstance().addListenerToCurrentThread(syncCompleteListener);
    }

    private BroadcastReceiver connectionBroadcastReceiver = new PebbleConnectionBroadcastReceiver();

    @Override
    public void reset() {
        if (null != this.pebbleAppUuid) {
            PebbleKit.closeAppOnPebble(this.mContext, this.pebbleAppUuid);
            delay(connectDelay);
        }
    }

    @Override
    public void disconnect() {
        if (null != this.pebbleAppUuid) {
            PebbleKit.closeAppOnPebble(this.mContext, this.pebbleAppUuid);
        }
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
        PebbleKit.startAppOnPebble(mContext, pebbleAppUuid);

        PebbleDeviceInfo pebbleDeviceInfo = readPebbleDeviceInfo();
        RxBus.getInstance().post(pebbleDeviceInfo);

        Device device = null;
        try {
            device = readPagareDeviceInfo();
        } catch (DeviceOperationException e) {
            RxBus.getInstance().post(new PaymentDeviceOperationFailed.Builder()
                    .reasonCode(e.getErrorCode())
                    .reason(e.getMessage())
                    .build());
            return;
        }
        RxBus.getInstance().post(device);
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

        //TODO the boilerplate of this method should be extracted to base class

        Log.d(TAG, "executeApduPackage executing on thread: " + Thread.currentThread());

        ApduExecutionResult apduExecutionResult = new ApduExecutionResult(apduPackage.getPackageId());
        apduExecutionResult.setExecutedTsEpoch(System.currentTimeMillis());

        int i = 0;
        for (ApduCommand apduCommand: apduPackage.getApduCommands()) {

            int currentSequenceNumber = apduCommand.getSequence();

            PebbleDictionary dict = new PebbleDictionary();
            dict.addString(MESSAGE_ID_APDU_COMMAND, Hex.bytesToHexString(apduCommand.getCommand()));
            dict.addString(MESSAGE_ID_APDU_SEQUENCE_NUMBER, "" + currentSequenceNumber);

            PebbleWrapper wrapper = new PebbleWrapper(mContext, PebblePagarePaymentDeviceConnector.this.pebbleAppUuid);
            PebbleDictionary response = null;
            try {
                response = wrapper.readData(dict, MESSAGE_ID_APDU_COMMAND + i);
            } catch (DeviceOperationException e) {
                Log.w(TAG, "apdu command was not processed");
                processApduCommandExecutionFailure(apduExecutionResult, e.getMessage());
                return;
            }
            if (null == response) {
                Log.w(TAG, "got null response to apdu command");
                processApduCommandExecutionFailure(apduExecutionResult, "Device did not respond");
                return;
            }

            String sequenceNumber = response.getString(MESSAGE_ID_APDU_SEQUENCE_NUMBER);
            if (null != sequenceNumber) {
                int responseSequenceNumber = Integer.parseInt(sequenceNumber);
                if (currentSequenceNumber != responseSequenceNumber) {
                    //TODO got responses out of order
                    Log.e(TAG, "got apdu responses out of order.  expected: " + currentSequenceNumber + ", got: " + responseSequenceNumber);
                    processApduCommandExecutionFailure(apduExecutionResult, "Device provided response out of order");
                    return;
                }
            } else {
                Log.e(TAG, "no sequence number provide in response");
                processApduCommandExecutionFailure(apduExecutionResult, "Device response did not contain a sequence number");
                return;
            }

            String apduResponse = response.getString(MESSAGE_ID_APDU_COMMAND_RESULT);
            if (null == apduResponse) {
                processApduCommandExecutionFailure(apduExecutionResult, "Device provided invalid response.  Apdu result does not contain a command result");
                return;
            } else if (apduResponse.length() < 4) {
                //TODO apdu response not correct length
                Log.e(TAG, "apdu command response does not have the correct length: " + apduResponse.length());
                processApduCommandExecutionFailure(apduExecutionResult, "Device provided invalid response.  Apdu result does not contain a response code");
                return;
            }

            ApduCommandResult commandResult = new ApduCommandResult.Builder()
                    .setCommandId(apduCommand.getCommandId())
                    .setResponseCode(apduResponse.substring(apduResponse.length() - 4))
                    .setResponseData(apduResponse)
                    .build();
            apduExecutionResult.addResponse(commandResult);

            //TODO only continue to next command if this one was successful

        }
        apduExecutionResult.deriveState();
        apduExecutionResult.setExecutedDurationTilNow();;

        RxBus.getInstance().post(apduExecutionResult);
    }

    private void processApduCommandExecutionFailure(ApduExecutionResult apduExecutionResult, String errorReason) {
        apduExecutionResult.setState(ResponseState.FAILED);
        apduExecutionResult.setExecutedDurationTilNow();
        apduExecutionResult.setErrorReason(errorReason);
        RxBus.getInstance().post(apduExecutionResult);
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

    private PebbleDeviceInfo readPebbleDeviceInfo() {
        PebbleKit.FirmwareVersionInfo info = PebbleKit.getWatchFWVersion(mContext);
        boolean appMessageSupported = PebbleKit.areAppMessagesSupported(mContext);
        PebbleDeviceInfo pebble = new PebbleDeviceInfo.Builder()
                .setFirmwareVersion(info.getMajor() + "." + info.getMinor() + "." + info.getPoint())
                .setAppMessageSupported(appMessageSupported)
                .build();
        return pebble;
    }

    private Device readPagareDeviceInfo() throws DeviceOperationException {
        PebbleWrapper wrapper = new PebbleWrapper(mContext, PebblePagarePaymentDeviceConnector.this.pebbleAppUuid);
        PebbleDictionary dict = new PebbleDictionary();
        dict.addString(MESSAGE_ID_DEVICE_INFO_DEVICE_TYPE, "");
        PebbleDictionary response = wrapper.readData(dict, MESSAGE_ID_DEVICE_INFO_DEVICE_TYPE);
        if (null == response) {
            Log.e(TAG, "read device info was not successful.  reasonCode: " + wrapper.getReasonCode() + ", reason: " + wrapper.getReason());
            throw new DeviceOperationException(wrapper.getReason(), wrapper.getReasonCode());
        }
        return getDeviceFromDictionary(response, DeviceTypes.WATCH);
    }

    private Device getDeviceFromDictionary(PebbleDictionary dict, String deviceType) {
        return  new Device.Builder()
                .setDeviceType(deviceType)
                .setDeviceName(dict.getString(MESSAGE_ID_DEVICE_INFO_DEVICE_NAME))
                .setManufacturerName(dict.getString(MESSAGE_ID_DEVICE_INFO_MANUFACTURER_NAME))
                .setModelNumber(dict.getString(MESSAGE_ID_DEVICE_INFO_MODEL_NUMBER))
                .setSerialNumber(dict.getString(MESSAGE_ID_DEVICE_INFO_SERIAL_NUMBER))
                .setBdAddress(dict.getString(MESSAGE_ID_DEVICE_INFO_BD_ADDRESS))
                .setFirmwareRevision(dict.getString(MESSAGE_ID_DEVICE_INFO_FIRMWARE_REVISION))
                .setHardwareRevision(dict.getString(MESSAGE_ID_DEVICE_INFO_HARDWARE_REVISION))
                .setSoftwareRevision(dict.getString(MESSAGE_ID_DEVICE_INFO_SOFTWARE_REVISION))
                .setLicenseKey(dict.getString(MESSAGE_ID_DEVICE_INFO_LICENSE_KEY))
                .setOSName(dict.getString(MESSAGE_ID_DEVICE_INFO_OS_NAME))
                .setSecureElementId(dict.getString(MESSAGE_ID_DEVICE_INFO_SECURE_ELEMENT_ID))
                .setSystemId(dict.getString(MESSAGE_ID_DEVICE_INFO_SYSTEM_ID))
                .build();
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

    private void sendWalletToPebble() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PebbleDictionary dict = getPebbleUpdateWalletMessage();
                if (null == dict || dict.size() == 0) {
                    Log.d(TAG, "wallet is empty");
                    dict = getPebbleEmptyWalletMessage();
                    dict.addString(0x2FFF, "Your wallet is empty.  Please add cards");
                } else {
                    dict.addString(0x2FFF, "Your wallet has been updated");
                }
                Log.d(TAG, "sending wallet update to pebble app.  transactionId: " + MESSAGE_ID_WALLET_UPDATE + ", size: " + dict.size());
                PebbleWrapper wrapper = new PebbleWrapper(PebblePagarePaymentDeviceConnector.this.mContext, PebblePagarePaymentDeviceConnector.this.pebbleAppUuid);
                try {
                    wrapper.writeData(dict, MESSAGE_ID_WALLET_UPDATE);
                    Log.d(TAG, "pebble wallet successfully updated");
                } catch (DeviceOperationException e) {
                    //TODO what is correct handling?   Want to retry until successful - what mechanism?
                    Log.d(TAG, "pebble wallet was not updated");
                }
            }
        };
        new Thread(runnable).start();
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

    private void delay(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // no consequences
        }
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

