package com.fitpay.android.wearable.model;

import android.util.Pair;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.apdu.ApduPackageResponse;

/**
 * Created by Vlad on 05.04.2016.
 */
public class ApduPair extends Pair<ApduPackage, ApduPackageResponse> {
    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public ApduPair(ApduPackage first, ApduPackageResponse second) {
        super(first, second);
    }
}
