package com.fitpay.android.api.models;


public class RelationshipCard {

    /**
     * description : The credit card id
     */
    private String creditCardId;

    /**
     * description : The credit card number, also known as a Primary Account Number (PAN)
     */
    private String pan;

    /**
     * description : The credit card expiration month
     */
    private int expMonth;

    /**
     * description : The credit card expiration year
     */
    private int expYear;

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public String getPan() {
        return pan;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

}