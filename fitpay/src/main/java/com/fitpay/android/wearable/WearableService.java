package com.fitpay.android.wearable;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.wearable.enums.States;
import com.fitpay.android.wearable.interfaces.IWearable;
import com.orhanobut.logger.Logger;

public class WearableService extends Service {

    private IWearable mWearable;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WearableService getService() {
            return WearableService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWearable != null) {
            mWearable.close();
        }
    }

    public void pairWithDevice(IWearable wearable) {
        mWearable = wearable;

        if (mWearable.isInitialized()) {
            mWearable.connect();
        } else {
            Logger.e("Wearable is not initialized");
        }
    }

    public void disconnect() {
        if (mWearable != null) {
            mWearable.disconnect();
        }
    }

    public void getDeviceInfo() {
        mWearable.getDeviceInfo();
    }

    public void getNFCState() {
        mWearable.getNFCState();
    }

    public void setNFCState(@States.NFC byte state) {
        mWearable.setNFCState(state);
    }

    public void sendApduPackage(ApduPackage apduPackage) {
        mWearable.sendApduPackage(apduPackage);
    }

    public void setSecureElementState(@States.SecureElement byte state) {
        mWearable.setSecureElementState(state);
    }
}
