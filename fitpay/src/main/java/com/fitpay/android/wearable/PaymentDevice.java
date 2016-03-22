package com.fitpay.android.wearable;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.PaymentDeviceAlert;
import com.fitpay.android.utils.Unit;

import java.util.ArrayList;
import java.util.List;

public final class PaymentDevice extends Unit{

    private Device mDevice;

    private List<AlertsListener> mAlertListeners;

    public PaymentDevice(){
        mAlertListeners = new ArrayList<>();
    }

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

    public void addAlertListener(AlertsListener listener) {
        mAlertListeners.add(listener);
    }

    public void removeAlertListener(AlertsListener listener) {
        mAlertListeners.remove(listener);
    }

    public interface AlertsListener{
        void handleAlert(@PaymentDeviceAlert.Type String alert);
    }
}