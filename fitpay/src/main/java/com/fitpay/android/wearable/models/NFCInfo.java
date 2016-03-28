package com.fitpay.android.wearable.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 28.03.2016.
 */
public class NFCInfo {

    private static final byte DISABLED = 0x00;
    private static final byte ENABLED = 0x01;

    private static final byte SUCCESS = 0x00;
    private static final byte FAIL_ENABLING = 0x01;
    private static final byte FAIL_DISABLING = 0x02;

    public static final int STATE_DISABLED = 0;
    public static final int STATE_ENABLED = STATE_DISABLED + 1;

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL_ENABLING = STATUS_SUCCESS + 1;
    public static final int STATUS_FAIL_DISABLING = STATUS_FAIL_ENABLING + 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_SUCCESS, STATUS_FAIL_ENABLING, STATUS_FAIL_DISABLING})
    public @interface Status {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_DISABLED, STATE_ENABLED})
    public @interface State {
    }

    private final
    @State
    int state;
    private final
    @Status
    int status;

    public NFCInfo(byte[] data) {
        switch (data[0]) {
            default:
            case DISABLED:
                state = STATE_DISABLED;
                break;

            case ENABLED:
                state = STATE_ENABLED;
                break;
        }

        switch (data[1]) {
            default:
            case SUCCESS:
                status = STATUS_SUCCESS;
                break;

            case FAIL_DISABLING:
                status = STATUS_FAIL_DISABLING;
                break;

            case FAIL_ENABLING:
                status = STATUS_FAIL_ENABLING;
                break;
        }
    }

    @State
    public int getState() {
        return state;
    }

    @Status
    public int getStatus() {
        return status;
    }

    public static byte[] convertStateToByte(@State int state) {
        return new byte[]{state == STATE_DISABLED ? DISABLED : ENABLED};
    }
}
