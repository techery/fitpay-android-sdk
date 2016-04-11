package com.fitpay.android.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vlad on 11.04.2016.
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