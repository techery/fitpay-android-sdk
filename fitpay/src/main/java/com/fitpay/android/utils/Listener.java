package com.fitpay.android.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for listener. Uses in {@link NotificationManager}
 */
public abstract class Listener {
    public Map<Class, Command> mCommands;

    public Listener() {
        mCommands = new HashMap<>();
    }

    public Map<Class, Command> getCommands() {
        return mCommands;
    }
}