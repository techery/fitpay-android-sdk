package com.fitpay.android.paymentdevice.impl;

import android.content.Context;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.apdu.ApduCommand;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.TopOfWallet;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.callbacks.ApduExecutionListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.events.CommitFailed;
import com.fitpay.android.paymentdevice.events.CommitSkipped;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.utils.ApduExecException;
import com.fitpay.android.utils.EventCallback;
import com.fitpay.android.utils.FPLog;
import com.fitpay.android.utils.Hex;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.TimestampUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import rx.Observable;

import static com.fitpay.android.paymentdevice.constants.ApduConstants.NORMAL_PROCESSING;
import static com.fitpay.android.utils.Constants.APDU_DATA;

/**
 * Base model for wearable payment device
 * <p>
 * This component is designed to handle one operation at a time.  It is not thread safe.
 */
public abstract class PaymentDeviceConnector implements IPaymentDeviceConnector {

    private final static String TAG = PaymentDeviceConnector.class.getSimpleName();

    private static final int MAX_REPEATS = 0;

    protected Context mContext;
    protected String mAddress;
    @Connection.State
    protected int state;
    private Map<String, CommitHandler> commitHandlers;
    private ErrorPair mErrorRepeats;

    private ApduExecutionListener apduExecutionListener;
    private ApduCommandListener apduCommandListener;

    private Commit currentCommit;

    private boolean apduExecutionInProgress = false;
    private long curApduPgkNumber;
    private ApduPackage curApduPackage;
    private ApduCommand curApduCommand;
    private ApduExecutionResult apduExecutionResult;

    protected User user;

    public PaymentDeviceConnector() {
        state = States.NEW;
        addDefaultCommitHandlers();
    }

    public PaymentDeviceConnector(Context context) {
        this();
        mContext = context;
    }

    public PaymentDeviceConnector(Context context, String address) {
        this(context);
        mAddress = address;
    }

    @Override
    public void init(Properties props) {
        // null implementation - override concrete class as needed
    }

    @Override
    public void setContext(Context context) {
        this.mContext = context;
    }

    /**
     * All connectors should handle apdu processing.
     */
    protected void addDefaultCommitHandlers() {
        addCommitHandler(CommitTypes.APDU_PACKAGE, new ApduCommitHandler());
    }

    @Override
    public
    @Connection.State
    int getState() {
        return state;
    }

    @Override
    public void setState(@Connection.State int state) {
        FPLog.d(TAG, "connection state changed: " + state);
        this.state = state;
        RxBus.getInstance().post(new Connection(state));
    }

    @Override
    public String getMacAddress() {
        return mAddress;
    }

    @Override
    public void close() {
        disconnect();

        if (null != apduExecutionListener) {
            NotificationManager.getInstance().removeListener(apduExecutionListener);
            apduExecutionListener = null;
        }
    }

    @Override
    public void reset() {
        // subclasses to implement as needed
    }

    @Override
    public void reconnect() {
        connect();
    }

    @Override
    public void addCommitHandler(String commitType, CommitHandler handler) {
        if (null == commitHandlers) {
            commitHandlers = new HashMap<>();
        }
        commitHandlers.put(commitType, handler);
    }

    @Override
    public void removeCommitHandler(String commitType) {
        if (null == commitHandlers) {
            return;
        }
        commitHandlers.remove(commitType);
    }

    @Override
    public void processCommit(Commit commit) {
        FPLog.d(TAG, "processing commit on Thread: " + Thread.currentThread() + ", " + Thread.currentThread().getName());
        currentCommit = commit;
        if (null == commitHandlers) {
            return;
        }
        CommitHandler handler = commitHandlers.get(commit.getCommitType());
        if (null != handler) {
            handler.processCommit(commit);
        } else {
            FPLog.w(TAG, "No action taken for commit.  No handler defined for commit: " + commit);
            // still need to signal that processing of the commit has completed
            RxBus.getInstance().post(new CommitSuccess(commit));
        }
    }

    @Override
    public void syncInit() {
        if (null == apduExecutionListener) {
            apduExecutionListener = new ApduPackageListener();
            NotificationManager.getInstance().addListenerToCurrentThread(this.apduExecutionListener);
        }
        mErrorRepeats = null;
    }

    @Override
    public void syncComplete() {
        if (null != apduExecutionListener) {
            NotificationManager.getInstance().removeListener(this.apduExecutionListener);
            apduExecutionListener = null;
        }
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {
        if (apduExecutionInProgress) {
            FPLog.w(TAG, "apduPackage processing is already in progress");
            return;
        }

        apduExecutionInProgress = true;

        apduExecutionResult = new ApduExecutionResult(apduPackage.getPackageId());
        apduExecutionResult.setExecutedTsEpoch(System.currentTimeMillis());

        curApduPackage = apduPackage;
        curApduPgkNumber = System.currentTimeMillis();

        apduCommandListener = new ApduCommandListener();
        NotificationManager.getInstance().addListener(apduCommandListener);

        onPreExecuteApdu();
    }

    @Override
    public void onPreExecuteApdu() {
        executeNextApduCommand();
    }

    @Override
    public void onPostExecuteApdu() {
        completeApduPackageExecution();
    }

    /**
     * Execution has finished
     */
    protected void completeApduPackageExecution() {
        FPLog.i(APDU_DATA, "\\ApduPackageResult\\: " + apduExecutionResult);

        NotificationManager.getInstance().removeListener(apduCommandListener);

        curApduCommand = null;
        curApduPackage = null;

        apduExecutionResult.setExecutedDurationTilNow();
        RxBus.getInstance().post(apduExecutionResult);

        apduExecutionResult = null;

        apduExecutionInProgress = false;
    }

    /**
     * get next apdu command
     */
    protected void executeNextApduCommand() {
        ApduCommand nextCommand = curApduPackage.getNextCommand(curApduCommand);

        if (nextCommand != null) {
            FPLog.i(APDU_DATA, "\\ProcessNextCommand\\: " + nextCommand.toString());

            curApduCommand = nextCommand;
            executeApduCommand(curApduPgkNumber, nextCommand);
        } else {
            onPostExecuteApdu();
        }
    }

    /**
     * Get TOW data from the server
     */
    protected final void getTopOfWalletData() {
        user.getAllCreditCards().flatMap(creditCardCollection -> {
            List<TopOfWallet> tow = new ArrayList<>();
            if (creditCardCollection.getResults() != null) {
                for (CreditCard card : creditCardCollection.getResults()) {
                    if (card.getTOW() != null) {
                        tow.add(card.getTOW());
                    }
                }
            }
            return Observable.just(tow);
        }).subscribe(
                topOfWallets -> {
                    if (topOfWallets.size() > 0) {
                        executeTopOfWallet(topOfWallets);
                        RxBus.getInstance().post(topOfWallets);
                    }
                },
                throwable -> FPLog.e(TAG, "TOW execution error: " + throwable.getMessage()));
    }

    /**
     * Send apdu execution result to the server
     *
     * @param result apdu execution result
     */
    private void sendApduExecutionResult(final ApduExecutionResult result) {

        EventCallback.Builder builder = new EventCallback.Builder()
                .setCommand(EventCallback.APDU_COMMANDS_SENT)
                .setStatus(EventCallback.STATUS_OK)
                .setTimestamp(result.getExecutedTsEpoch());

        if (!result.getState().equals(ResponseState.PROCESSED)) {
            builder.setReason(result.getErrorReason()).setStatus(EventCallback.STATUS_FAILED);
        }

        builder.build().send();

        if (null != currentCommit) {
            currentCommit.confirm(result, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result2) {
                    @ResponseState.ApduState String state = result.getState();

                    switch (state) {
                        case ResponseState.PROCESSED:
                            RxBus.getInstance().post(new CommitSuccess(currentCommit));
                            break;
                        case ResponseState.EXPIRED:
                            RxBus.getInstance().post(new CommitSkipped(currentCommit));
                            break;
                        case ResponseState.FAILED:
                            // TODO: determine if apdu package is retryable or not, right now the
                            // FitPay API doesn't support this attribute, but it will!
                            RxBus.getInstance().post(new CommitSkipped(currentCommit));
                            break;
                        case ResponseState.ERROR:
                            // TODO: determine what to do here, retry immediatly?
                            RxBus.getInstance().post(new CommitFailed.Builder()
                                    .commit(currentCommit)
                                    .errorCode(999)         //TODO create enum for errors
                                    .errorMessage("apdu command failure")
                                    .build());
                            break;
                    }
                }

                @Override
                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                    // TODO: determine what to do here, a failure reporting the confirmation to
                    // FitPay isn't really a commit failure, it's something that needs to be
                    // managed properly... i.e. the commit was applied, it was successful, it was
                    // the reporting to FitPay that wasn't, is that part of the commit?
                    FPLog.e(TAG, "Could not post apduExecutionResult. " + errorCode + ": " + errorMessage);
                    RxBus.getInstance().post(new CommitFailed.Builder()
                            .commit(currentCommit)
                            .errorCode(errorCode)
                            .errorMessage("Could not send adpu confirmation.  cause: " + errorMessage)
                            .build());
                }
            });
        } else {
            FPLog.e(TAG, "Unexpected state - current commit is null but should be populated");
        }
    }

    private class ApduPackageListener extends ApduExecutionListener {

        @Override
        public void onApduPackageResultReceived(ApduExecutionResult result) {
            sendApduExecutionResult(result);
        }

        @Override
        public void onApduPackageErrorReceived(ApduExecutionResult result) {

            final String id = result.getPackageId();

            switch (result.getState()) {
                case ResponseState.EXPIRED:
                    sendApduExecutionResult(result);
                    break;

                default:  //retry error and failure
                    if (mErrorRepeats == null || !mErrorRepeats.id.equals(id)) {
                        mErrorRepeats = new ErrorPair(id, 0);
                    }

                    if (mErrorRepeats.count++ >= MAX_REPEATS) {
                        sendApduExecutionResult(result);
                    } else {
                        // retry
                        processCommit(currentCommit);
                    }
                    break;
            }
        }
    }

    private class ApduCommandListener extends Listener {
        final String normalResponseCode = Hex.bytesToHexString(NORMAL_PROCESSING);

        ApduCommandListener() {
            super();
            mCommands.put(ApduCommandResult.class, data -> onApduCommandReceived((ApduCommandResult) data));
            mCommands.put(ApduExecException.class, data -> onApduExecErrorReceived((ApduExecException) data));
        }

        private void onApduCommandReceived(ApduCommandResult apduCommandResult) {
            FPLog.i(APDU_DATA, "\\CommandProcessed\\: " + apduCommandResult);

            apduExecutionResult.addResponse(apduCommandResult);

            String responseCode = apduCommandResult.getResponseCode();
            if (responseCode.equals(normalResponseCode) || curApduCommand.isContinueOnFailure()) {
                executeNextApduCommand();
            } else {
                ApduExecException execException = new ApduExecException(
                        ResponseState.FAILED,
                        "Device provided invalid response code: " + responseCode,
                        apduCommandResult.getCommandId());
                onApduExecErrorReceived(execException);
            }
        }

        private void onApduExecErrorReceived(ApduExecException apduError) {
            apduExecutionResult.setState(apduError.getResponseState());
            apduExecutionResult.setErrorReason(apduError.getMessage());
            onPostExecuteApdu();
        }
    }

    private class ApduCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            if (payload instanceof ApduPackage) {
                ApduPackage pkg = (ApduPackage) payload;

                long validUntil = TimestampUtils.getDateForISO8601String(pkg.getValidUntil()).getTime();
                long currentTime = System.currentTimeMillis();

                FPLog.i(APDU_DATA, "\\ApduPackage\\: " + pkg);

                if (validUntil > currentTime) {
                    executeApduPackage(pkg);
                } else {
                    ApduExecutionResult result = new ApduExecutionResult(pkg.getPackageId());
                    result.setExecutedDuration(0);
                    result.setExecutedTsEpoch(currentTime);
                    result.setState(ResponseState.EXPIRED);

                    RxBus.getInstance().post(result);
                }
            } else {
                FPLog.e(TAG, "ApduCommitHandler called for non-adpu commit. THIS IS AN APPLICATION DEFECT " + commit);
            }
        }
    }

    private class ErrorPair {
        String id;
        int count;

        ErrorPair(String id, int count) {
            this.id = id;
            this.count = count;
        }
    }

}
