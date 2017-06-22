package com.fitpay.android.paymentdevice.models;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;

/**
 * Data for sync request
 */
public class SyncRequest {
    private User user;
    private Device device;
    private IPaymentDeviceConnector connector;

    private SyncRequest() {
    }

    public User getUser() {
        return user;
    }

    public Device getDevice() {
        return device;
    }

    public IPaymentDeviceConnector getConnector() {
        return connector;
    }

    public static final class Builder {
        private User user;
        private Device device;
        private IPaymentDeviceConnector connector;

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setDevice(Device device) {
            this.device = device;
            return this;
        }

        public Builder setConnector(IPaymentDeviceConnector connector) {
            this.connector = connector;
            return this;
        }

        public SyncRequest build() {
            SyncRequest request = new SyncRequest();
            request.user = user;
            request.device = device;
            request.connector = connector;
            return request;
        }
    }
}
