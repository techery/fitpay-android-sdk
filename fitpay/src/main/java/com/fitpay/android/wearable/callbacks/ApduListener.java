package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.wearable.utils.ApduPair;

/**
 * Created by Vlad on 07.04.2016.
 */
public interface ApduListener {
    void onApduPackageResultReceived(ApduPair pair);
}
