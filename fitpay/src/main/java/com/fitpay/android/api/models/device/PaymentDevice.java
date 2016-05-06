package com.fitpay.android.api.models.device;

import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.BaseModel;

/**
 * Payment device
 */
public class PaymentDevice extends BaseModel {

    /**
     * The type of device (PHONE, TABLET, ACTIVITY_TRACKER, SMARTWATCH, PC, CARD_EMULATOR, CLOTHING, JEWELRY, OTHER
     */
    @DeviceTypes.Type
    protected String deviceType;

    /**
     * The manufacturer name of the device.
     */
    protected String manufacturerName;

    /**
     * The name of the device model.
     */
    protected String deviceName;

    /**
     * description : The ID of a secure element in a payment capable device
     */
    protected SecureElement secureElement;

    protected PaymentDevice() {
    }

    @DeviceTypes.Type
    public String getDeviceType() {
        return deviceType;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getSecureElementId() {
        return secureElement != null ? secureElement.secureElementId : null;
    }

    /**
     * Secure element
     */
    static class SecureElement {
        final String secureElementId;

        SecureElement(String secureElementId) {
            this.secureElementId = secureElementId;
        }
    }
}
