package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.utils.Listener;
import com.fitpay.android.wearable.interfaces.IApduMessage;
import com.fitpay.android.wearable.utils.ApduPair;

/**
 * Created by Vlad on 07.04.2016.
 */
public abstract class ApduListener extends Listener implements IApduListener {
    public ApduListener() {
        super();
        mCommands.put(IApduMessage.class, data -> onApduPackageResultReceived((ApduPair) data));
    }
}
