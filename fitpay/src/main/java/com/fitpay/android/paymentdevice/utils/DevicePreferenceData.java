package com.fitpay.android.paymentdevice.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tgs on 5/14/16.
 */
public class DevicePreferenceData {

    private String deviceId;
    private String lastCommitId;
    private String paymentDeviceServiceType;

    private DevicePreferenceData() {

    }

    public static DevicePreferenceData loadFromPreferences(Context context, String deviceId) {
        SharedPreferences prefs = getPreferences(context, deviceId);
        DevicePreferenceData data = new Builder()
                .deviceId(deviceId)
                .lastCommitId(prefs.getString("lastCommitId", null))
                .paymentDeviceServiceType(prefs.getString("paymentDeviceServiceType", null))
                .build();
        return data;
    }

    public static void storePreferences(Context context, DevicePreferenceData data) {
        if (null == data.deviceId) {
            return;
        }
        SharedPreferences prefs = getPreferences(context, data.deviceId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastCommitId", data.lastCommitId);
        editor.putString("paymentDeviceServiceType", data.paymentDeviceServiceType);
        boolean success = editor.commit();
    }

    protected static SharedPreferences getPreferences(Context context, String deviceId) {
        return context.getSharedPreferences("paymentDevice_" + deviceId, Context.MODE_PRIVATE);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLastCommitId() {
        return lastCommitId;
    }

    public void setLastCommitId(String lastCommitId) {
        this.lastCommitId = lastCommitId;
    }

    public String getPaymentDeviceServiceType() {
        return paymentDeviceServiceType;
    }

    public void setPaymentDeviceServiceType(String paymentDeviceServiceType) {
        this.paymentDeviceServiceType = paymentDeviceServiceType;
    }

    public static class Builder {

        private String deviceId;
        private String lastCommitId;
        private String paymentDeviceServiceType;

        public Builder() {
        }

        public Builder deviceId(String deviceIdValue) {
            this.deviceId = deviceIdValue;
            return this;
        }

        public Builder lastCommitId(String lastCommitIdValue) {
            this.lastCommitId = lastCommitIdValue;
            return this;
        }

        public Builder paymentDeviceServiceType(String paymentDeviceServiceTypeValue) {
            this.paymentDeviceServiceType = paymentDeviceServiceTypeValue;
            return this;
        }

        public DevicePreferenceData build() {
            DevicePreferenceData data = new DevicePreferenceData();
            data.deviceId = this.deviceId;
            data.lastCommitId = this.lastCommitId;
            data.paymentDeviceServiceType = this.paymentDeviceServiceType;
            return data;
        }


    }
}
