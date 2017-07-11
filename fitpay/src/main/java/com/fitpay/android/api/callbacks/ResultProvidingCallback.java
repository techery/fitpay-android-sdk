package com.fitpay.android.api.callbacks;

import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.utils.FPLog;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tgs on 4/27/16.
 */
public class ResultProvidingCallback<T> implements ApiCallback<T> {

        private T result;
        private int errorCode = -1;
        private String errorMessage;
        private CountDownLatch latch;

        public ResultProvidingCallback() {}

        public ResultProvidingCallback(CountDownLatch latch) {
            this();
            this.latch = latch;
        }

        @Override
        public void onSuccess(T result) {
            this.result = result;
            if (null != latch) {
                latch.countDown();
            }
        }

        @Override
        public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
            FPLog.w("callback failure, errorCode: " + errorCode + ", errorMessage: " + errorMessage);

            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            if (null != latch) {
                latch.countDown();
            }
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public T getResult() {
            return this.result;
        }

}
