package com.fitpay.android.api.models;


public class Reason {

    /**
     * description : Identifies the party initiating the deactivation request
     * enum : ["CARDHOLDER","ISSUER"]
     */

    private String causedBy;
    /**
     * description : The reason that the credit card is to deactivated
     */
    private String reason;

    public String getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(String causedBy) {
        this.causedBy = causedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}