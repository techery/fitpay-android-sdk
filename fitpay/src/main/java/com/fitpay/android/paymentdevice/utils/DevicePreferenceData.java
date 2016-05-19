package com.fitpay.android.paymentdevice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tgs on 5/14/16.
 */
public class DevicePreferenceData {

    private static final String[] KEY_VALUES = { "lastCommitId", "paymentDeviceServiceType", "paymentDeviceConfig"};

    private String deviceId;
    private String lastCommitId;
    private String paymentDeviceServiceType;
    private String paymentDeviceConfig;
    private Map<String, String> additionalValues;

    private DevicePreferenceData() {
        additionalValues = new HashMap<>();
    }

    public static DevicePreferenceData loadFromPreferences(Context context, String deviceId) {
        SharedPreferences prefs = getPreferences(context, deviceId);
        Map<String, String> values = new HashMap<>();
        for (String key: prefs.getAll().keySet()) {
            if (!Arrays.asList(KEY_VALUES).contains(key)) {
                values.put(key, prefs.getString(key, null));
            }
        }

        DevicePreferenceData data = new Builder()
                .deviceId(deviceId)
                .lastCommitId(prefs.getString("lastCommitId", null))
                .paymentDeviceServiceType(prefs.getString("paymentDeviceServiceType", null))
                .paymentDeviceConfig(prefs.getString("paymentDeviceConfig", null))
                .additionalValues(values)
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
        editor.putString("paymentDeviceConfig", data.paymentDeviceConfig);
        for (String key: data.additionalValues.keySet()) {
            editor.putString(key, data.additionalValues.get(key));
        }
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

    public String getPaymentDeviceConfig() {
        return paymentDeviceConfig;
    }

    public void setPaymentDeviceServiceType(String paymentDeviceServiceType) {
        this.paymentDeviceServiceType = paymentDeviceServiceType;
    }

    public void putAdditionalValue(String key, String value) {
        additionalValues.put(key, value);
    }

    public void removeAdditionalValue(String key) {
        additionalValues.remove(key);
    }

    public static class Builder {

        private String deviceId;
        private String lastCommitId;
        private String paymentDeviceServiceType;
        private String paymentDeviceConfig;
        private Map<String, String> additionalValues;

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

        public Builder paymentDeviceConfig(String paymentDeviceConfig) {
            this.paymentDeviceConfig = paymentDeviceConfig;
            return this;
        }

        public Builder additionalValues(Map<String, String> additionalValues) {
            this.additionalValues = additionalValues;
            return this;
        }

        public DevicePreferenceData build() {
            DevicePreferenceData data = new DevicePreferenceData();
            data.deviceId = this.deviceId;
            data.lastCommitId = this.lastCommitId;
            data.paymentDeviceServiceType = this.paymentDeviceServiceType;
            data.paymentDeviceConfig = this.paymentDeviceConfig;
            data.additionalValues = this.additionalValues;
            return data;
        }


    }
}
