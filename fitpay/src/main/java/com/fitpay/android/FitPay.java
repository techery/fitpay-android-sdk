package com.fitpay.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fitpay.android.utils.Unit;

import java.util.HashMap;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class FitPay {

    private static FitPay sInstance;

    private Context mContext;
    private HashMap<String, Unit> mUnits;

    private FitPay(@NonNull Context context) {
        mContext = context;
    }

    public static FitPay getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Init FitPay first");
        }

        return sInstance;
    }

    public static FitPay init(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new FitPay(context);
            sInstance.mUnits = new HashMap<>();
        }
        return sInstance;
    }

    public <T extends Unit> FitPay addUnit(T unit) {

        String unitName = unit.getName();

        if (!mUnits.containsKey(unitName)) {
            mUnits.put(unitName, unit);
            unit.onAdd();
        }

        return this;
    }

    public FitPay removeUnit(Class clazz) {
        String unitName = clazz.getName();

        if (mUnits.containsKey(unitName)) {
            Unit unit = mUnits.get(unitName);
            unit.onRemove();
            mUnits.remove(unitName);
        }

        return this;
    }

    public <T extends Unit> T getUnit(Class<T> clazz) {
        String name = clazz.getName();

        if (mUnits.containsKey(name)) {
            Unit unit = mUnits.get(name);
            return (T) unit;
        }

        return null;
    }
}
