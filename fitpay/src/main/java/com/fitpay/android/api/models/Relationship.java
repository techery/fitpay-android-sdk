package com.fitpay.android.api.models;

public class Relationship extends BaseModel{

    /**
     * JSON Web Encrypted compact serialization of the credit card's information from
     * @see com.fitpay.android.api.models.CreditCard.CreditCardInfo
     */
    private String encryptedData;
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}