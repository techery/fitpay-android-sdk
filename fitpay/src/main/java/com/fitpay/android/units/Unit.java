package com.fitpay.android.units;

/**
 * Created by Vlad on 12.02.2016.
 */
public abstract class Unit {
    public Unit() {
    }

    public void onAdd(){
    }

    public void onRemove(){
    }

    public String getName() {
        return getClass().getName();
    }
}
