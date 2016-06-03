package com.fitpay.android.paymentdevice.constants;

/**
 * Payment device states
 */
public final class States {
    public static final byte DISABLE = (byte) 0x00;
    public static final byte ENABLE = (byte) 0x01;
    public static final byte DONT_CHANGE = (byte) 0xFF;

    public static final byte POWER_OFF = (byte) 0x00;
    public static final byte POWER_ON = (byte) 0x02;
    public static final byte RESET = (byte) 0x01;

    public static final int NEW = -1;
    public static final int DISCONNECTED = 0;
    public static final int CONNECTED = 1;
    public static final int CONNECTING = 2;
    public static final int DISCONNECTING = 3;
    public static final int INITIALIZED = 4;

    public static final int STARTED = 0;
    public static final int COMPLETED = 1;
    public static final int FAILED = 2;
    public static final int IN_PROGRESS = 3;
    public static final int INC_PROGRESS = 4;
    public static final int COMMIT_COMPLETED = 5;

}
