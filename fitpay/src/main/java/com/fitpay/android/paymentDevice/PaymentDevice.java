package com.fitpay.android.paymentDevice;

import com.fitpay.android.models.Device;
import com.google.gson.annotations.SerializedName;

public class PaymentDevice {

    public enum PaymentDeviceAlert {

        @SerializedName("${TransactionAlert}")
        TRANSACTIONALERT("${TransactionAlert}"),

        @SerializedName("${SecurityAlert}")
        SecurityAlert("${SecurityAlert}"),

        @SerializedName("${ConnectionAlert}")
        CONNECTIONALERT("${ConnectionAlert}");

        String alert;

        PaymentDeviceAlert(String alert) {
            this.alert = alert;
        }
    }

    private Device mDevice;


    /**
     * Provides current device information
     */
    public Device getDevice() {
        return mDevice;
    }

    /**
     * Sets current device information
     *
     * @param device Device object
     */
    public void setDevice(Device device) {
        this.mDevice = device;
    }


}