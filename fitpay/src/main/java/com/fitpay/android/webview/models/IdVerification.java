package com.fitpay.android.webview.models;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.RtmMessageResponse;

/**
 * IdVerification data
 */

public class IdVerification {
    private Integer oemAccountInfoUpdatedDate;
    private Integer oemAccountCreatedDate;
    private Integer suspendedCardsInAccount;
    private Integer daysSinceLastAccountActivity;
    private Integer deviceLostMode;
    private Integer deviceWithActiveTokens;
    private Integer activeTokenOnAllDevicesForAccount;
    private Integer accountScore; // int between 0-9
    private Integer deviceScore; // int between 0-9
    private Boolean nfcCapable;

    private IdVerification() {
    }

    /**
     * send data to RTM
     */
    public void send() {
        RxBus.getInstance().post(new RtmMessageResponse(this, RtmType.ID_VERIFICATION));
    }

    public class Builder {
        private Integer oemAccountInfoUpdatedDate;
        private Integer oemAccountCreatedDate;
        private Integer suspendedCardsInAccount;
        private Integer daysSinceLastAccountActivity;
        private Integer deviceLostMode;
        private Integer deviceWithActiveTokens;
        private Integer activeTokenOnAllDevicesForAccount;
        private Integer accountScore; // int between 0-9
        private Integer deviceScore; // int between 0-9
        private Boolean nfcCapable;

        public Builder setOemAccountInfoUpdatedDate(int oemAccountInfoUpdatedDate) {
            this.oemAccountInfoUpdatedDate = oemAccountInfoUpdatedDate;
            return this;
        }

        public Builder setOemAccountCreatedDate(int oemAccountCreatedDate) {
            this.oemAccountCreatedDate = oemAccountCreatedDate;
            return this;
        }

        public Builder setSuspendedCardsInAccount(int suspendedCardsInAccount) {
            this.suspendedCardsInAccount = suspendedCardsInAccount;
            return this;
        }

        public Builder setDaysSinceLastAccountActivity(int daysSinceLastAccountActivity) {
            this.daysSinceLastAccountActivity = daysSinceLastAccountActivity;
            return this;
        }

        public Builder setDeviceLostMode(int deviceLostMode) {
            this.deviceLostMode = deviceLostMode;
            return this;
        }

        public Builder setDeviceWithActiveTokens(int deviceWithActiveTokens) {
            this.deviceWithActiveTokens = deviceWithActiveTokens;
            return this;
        }

        public Builder setActiveTokenOnAllDevicesForAccount(int activeTokenOnAllDevicesForAccount) {
            this.activeTokenOnAllDevicesForAccount = activeTokenOnAllDevicesForAccount;
            return this;
        }

        /**
         * Set account score.
         * @param accountScore Between 0-9
         * @return this
         */
        public Builder setAccountScore(int accountScore) {
            if(accountScore < 0 && accountScore > 9){
                throw new IllegalArgumentException("accountScore should be between 0-9");
            }
            this.accountScore = accountScore;
            return this;
        }

        /**
         * Set device score.
         * @param deviceScore Between 0-9
         * @return this
         */
        public Builder setDeviceScore(int deviceScore) {
            if(deviceScore < 0 && deviceScore > 9){
                throw new IllegalArgumentException("deviceScore should be between 0-9");
            }
            this.deviceScore = deviceScore;
            return this;
        }

        public Builder setNfcCapable(boolean nfcCapable) {
            this.nfcCapable = nfcCapable;
            return this;
        }

        public IdVerification build(){
            IdVerification idVerification = new IdVerification();
            idVerification.accountScore = accountScore;
            idVerification.oemAccountInfoUpdatedDate = oemAccountInfoUpdatedDate;
            idVerification.oemAccountCreatedDate = oemAccountCreatedDate;
            idVerification.suspendedCardsInAccount = suspendedCardsInAccount;
            idVerification.daysSinceLastAccountActivity = daysSinceLastAccountActivity;
            idVerification.deviceLostMode = deviceLostMode;
            idVerification.deviceWithActiveTokens = deviceWithActiveTokens;
            idVerification.activeTokenOnAllDevicesForAccount = activeTokenOnAllDevicesForAccount;
            idVerification.accountScore = accountScore;
            idVerification.deviceScore = deviceScore;
            idVerification.nfcCapable = nfcCapable;
            return idVerification;
        }
    }
}
