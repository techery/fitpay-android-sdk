package com.fitpay.android.api.models.device;

import com.fitpay.android.api.enums.ResponseState;

/**
 * Created by ssteveli on 8/16/17.
 */

public class CommitConfirm {
    @ResponseState.CommitState
    private final String result;

    public CommitConfirm(@ResponseState.CommitState String result) {
        this.result = result;
    }

    @ResponseState.CommitState
    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "CommitConfirm{" +
                "result='" + result + '\'' +
                '}';
    }
}
