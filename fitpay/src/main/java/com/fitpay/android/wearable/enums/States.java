package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 04.04.2016.
 */
public class States {
    public static final byte DISABLE = (byte) 0x00;
    public static final byte ENABLE = (byte) 0x01;
    public static final byte DONT_CHANGE = (byte) 0xFF;

    public static final byte POWER_OFF = (byte) 0x00;
    public static final byte POWER_ON = (byte) 0x02;
    public static final byte RESET = (byte) 0x01;

    public static final int DISCONNECTED = 0;
    public static final int CONNECTED = 1;
    public static final int CONNECTING = 2;
    public static final int DISCONNECTING = 3;
    public static final int INITIALIZED = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ENABLE, DISABLE, DONT_CHANGE})
    public @interface NFC {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POWER_OFF, POWER_ON, RESET})
    public @interface SecureElement {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING, INITIALIZED})
    public @interface Wearable {
    }
}
