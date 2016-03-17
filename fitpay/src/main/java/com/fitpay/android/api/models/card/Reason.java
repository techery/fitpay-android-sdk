package com.fitpay.android.api.models.card;


import com.fitpay.android.api.enums.CardInitiators;

public final class Reason {

    @CardInitiators.Initiator
    private String causedBy;
    /**
     * description : The reason that the credit card is to deactivated
     */
    private String reason;

    @CardInitiators.Initiator
    public String getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(@CardInitiators.Initiator String causedBy) {
        this.causedBy = causedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}