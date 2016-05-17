package com.fitpay.android.paymentdevice.impl.pagare;

/**
 * Created by tgs on 5/16/16.
 */
public class PebbleDeviceInfo {

    private String firmwareVersion;
    private boolean appMessageSupported;

    public boolean isAppMessageSupported() {
        return appMessageSupported;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public static class Builder {

        private String firmwareVersion;
        private boolean appMessageSupported = false;

        public Builder setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder setAppMessageSupported(boolean appMessageSupported) {
            this.appMessageSupported = appMessageSupported;
            return this;
        }

        public PebbleDeviceInfo build() {
            PebbleDeviceInfo device = new PebbleDeviceInfo();
            device.firmwareVersion = this.firmwareVersion;
            device.appMessageSupported = appMessageSupported;
            return device;
        }

    }
}
