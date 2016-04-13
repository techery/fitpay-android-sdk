package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.wearable.interfaces.IApduMessage;

/**
 * Created by Vlad on 07.04.2016.
 */
public abstract class ApduExecListener extends Listener implements IListeners.ApduListener {
    public ApduExecListener() {
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
    }
}
