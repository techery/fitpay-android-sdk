package com.fitpay.android.paymentDevice;

import com.fitpay.android.api.models.Device;
import com.fitpay.android.paymentDevice.enums.PaymentDeviceAlert;
import com.fitpay.android.utils.Unit;

import java.util.ArrayList;
import java.util.List;

public final class DeviceUnit extends Unit{

    private Device mDevice;

    private List<AlertsListener> mAlertListeners;

    public DeviceUnit(){
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
        void handleAlert(PaymentDeviceAlert alert);
    }
}