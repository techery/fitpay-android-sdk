package com.fitpay.android.paymentdevice.models;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;

import java.util.UUID;

/**
 * Data for sync request
 */
public class SyncRequest {
    private final String syncId = UUID.randomUUID().toString();
    private final User user;
    private final Device device;
    private final boolean useLastAckCommit;
    private final IPaymentDeviceConnector connector;

    private SyncRequest(User user, Device device, boolean useLastAckCommit, IPaymentDeviceConnector connector) {
        this.user = user;
        this.device = device;
        this.useLastAckCommit = useLastAckCommit;
        this.connector = connector;
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

    public String getSyncId() {
        return syncId;
    }

    public boolean useLastAckCommit() {
        return useLastAckCommit;
    }

    @Override
    public String toString() {
        return "SyncRequest{" +
                "syncId='" + syncId + '\'' +
                ", user=" + user +
                ", device=" + device +
                ", useLastAckCommit=" + useLastAckCommit +
                ", connector=" + connector +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private User user;
        private Device device;
        private boolean useLastAckCommit = true;
        private IPaymentDeviceConnector connector;

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setDevice(Device device) {
            this.device = device;
            return this;
        }

        public Builder setUseLastAckCommit(boolean useLastAckCommit) {
            this.useLastAckCommit = useLastAckCommit;
            return this;
        }

        public Builder setConnector(IPaymentDeviceConnector connector) {
            this.connector = connector;
            return this;
        }

        public SyncRequest build() {
            return new SyncRequest(user, device, useLastAckCommit, connector);
        }
    }
}
