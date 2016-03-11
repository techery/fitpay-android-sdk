package com.fitpay.android.api.models;

import com.fitpay.android.api.enums.DeviceTypes;

/**
 * Created by Vlad on 09.03.2016.
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

    protected PaymentDevice(){
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

    static class SecureElement{
        String secureElementId;

        protected SecureElement(String secureElementId){
            this.secureElementId = secureElementId;
        }
    }
}
