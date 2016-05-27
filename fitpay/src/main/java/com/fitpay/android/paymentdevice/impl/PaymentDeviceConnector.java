package com.fitpay.android.paymentdevice.impl;

import android.content.Context;
import android.util.Log;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.TimestampUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Base model for wearable payment device
 */
public abstract class PaymentDeviceConnector implements IPaymentDeviceConnector {

    private final static String TAG = PaymentDeviceConnector.class.getSimpleName();

    protected Context mContext;
    protected String mAddress;
    protected @Connection.State int state;
    protected Map<String, CommitHandler> commitHandlers;

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
    public @Connection.State int getState() {
        return state;
    }

    @Override
    public void setState(@Connection.State int state) {
        Log.d(TAG, "connection state changed: " + state);
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
            commitHandlers =  new HashMap<>();
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
        Log.d(TAG, "processing commit on Thread: " + Thread.currentThread() + ", " + Thread.currentThread().getName());
        if (null == commitHandlers) {
            return;
        }
        CommitHandler handler = commitHandlers.get(commit.getCommitType());
        if (null != handler) {
            handler.processCommit(commit);
        } else {
            Log.d(TAG, "No action taken for commit.  No handler defined for commit: " + commit);
            // still need to signal that processing of the commit has completed
            RxBus.getInstance().post(new CommitSuccess(commit.getCommitId()));
        }
    }


    @Override
    public void syncInit() {
        // null implementation - override in implementation class as needed
    }

    @Override
    public void syncComplete() {
        // null implementation - override in implementation class as needed
    }

    private class ApduCommitHandler implements CommitHandler {

        @Override
        public void processCommit(Commit commit) {
            Object payload = commit.getPayload();
            if (payload instanceof ApduPackage) {
                ApduPackage pkg = (ApduPackage) payload;

                long validUntil = TimestampUtils.getDateForISO8601String(pkg.getValidUntil()).getTime();
                long currentTime = System.currentTimeMillis();

                if(validUntil > currentTime){
                    PaymentDeviceConnector.this.executeApduPackage(pkg);
                } else {
                    ApduExecutionResult result = new ApduExecutionResult(pkg.getPackageId());
                    result.setExecutedDuration(0);
                    result.setExecutedTsEpoch(currentTime);
                    result.setState(ResponseState.EXPIRED);

                    RxBus.getInstance().post(result);
                }
            } else {
                Log.e(TAG, "ApduCommitHandler called for non-adpu commit.  THIS IS AN APPLICTION DEFECT " + commit);
            }
        }
    }
}
