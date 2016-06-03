package com.fitpay.android.paymentdevice.impl.pagare.model;

/**
 * Created by tgs on 5/17/16.
 */
public class WalletEntry {


    private String pan;
    private int expYear;
    private int expMonth;
    private String cardType;
    private boolean active;
    private boolean isDefault;
    private boolean mostRecentTouch;

    private WalletEntry() {}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isMostRecentTouch() {
        return mostRecentTouch;
    }

    public void setMostRecentTouch(boolean mostRecentTouch) {
        this.mostRecentTouch = mostRecentTouch;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    @Override
    public String toString() {
        return "WalletEntry{" +
                "active=" + active +
                ", pan='" + pan + '\'' +
                ", expYear=" + expYear +
                ", expMonth=" + expMonth +
                ", cardType='" + cardType + '\'' +
                ", isDefault=" + isDefault +
                ", mostRecentTouch=" + mostRecentTouch +
                '}';
    }

    public static class Builder {
        private String pan;
        private int expYear;
        private int expMonth;
        private String cardType;
        private boolean active;
        private boolean isDefault;
        private boolean mostRecentTouch;

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setCardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder setExpMonth(int expMonth) {
            this.expMonth = expMonth;
            return this;
        }

        public Builder setExpYear(int expYear) {
            this.expYear = expYear;
            return this;
        }

        public Builder setDefault(boolean aDefault) {
            isDefault = aDefault;
            return this;
        }

        public Builder setMostRecentTouch(boolean mostRecentTouch) {
            this.mostRecentTouch = mostRecentTouch;
            return this;
        }

        public Builder setPan(String pan) {
            this.pan = pan;
            return this;
        }

        public WalletEntry build() {
            WalletEntry walletEntry = new WalletEntry();
            walletEntry.pan = this.pan;
            walletEntry.cardType = this.cardType;
            walletEntry.expMonth = this.expMonth;
            walletEntry.expYear = this.expYear;
            walletEntry.active = this.active;
            walletEntry.isDefault = this.isDefault;
            walletEntry.mostRecentTouch = this.mostRecentTouch;
            return walletEntry;
        }
    }

}
