package com.fitpay.android.paymentdevice.models;

import com.fitpay.android.api.enums.SyncInitiator;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.sync.SyncLinks;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;
import com.fitpay.android.utils.StringUtils;

import java.util.UUID;

/**
 * Data for sync request
 */
public final class SyncRequest {
    private final String syncId;
    private final User user;
    private final Device device;
    private final boolean useLastAckCommit;
    private final IPaymentDeviceConnector connector;
    private final SyncLinks syncLinks;
    @SyncInitiator.Initiator
    private final String syncInitiator;

    private SyncRequest(
            String syncId,
            User user,
            Device device,
            boolean useLastAckCommit,
            IPaymentDeviceConnector connector,
            SyncLinks syncLinks,
            @SyncInitiator.Initiator String initiator) {
        this.syncId = !StringUtils.isEmpty(syncId) ? syncId : UUID.randomUUID().toString();
        this.user = user;
        this.device = device;
        this.useLastAckCommit = useLastAckCommit;
        this.connector = connector;
        this.syncLinks = syncLinks;
        this.syncInitiator = initiator;
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

    public @SyncInitiator.Initiator
    String getSyncInitiator() {
        return syncInitiator;
    }

    public SyncLinks getSyncLinks() {
        return syncLinks;
    }

    @Override
    public String toString() {
        return "SyncRequest{" +
                "syncId='" + syncId + '\'' +
                ", user=" + user +
                ", device=" + device +
                ", useLastAckCommit=" + useLastAckCommit +
                ", connector=" + connector +
                ", links=" + syncLinks +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String syncId;
        private User user;
        private Device device;
        private boolean useLastAckCommit = true;
        private IPaymentDeviceConnector connector;
        private SyncLinks syncLinks;
        @SyncInitiator.Initiator
        private String initiator;

        /**
         * Set sync id
         *
         * @param syncId sync id
         * @return this
         */
        public Builder setSyncId(String syncId) {
            this.syncId = syncId;
            return this;
        }

        /**
         * Set current user
         *
         * @param user current user
         * @return this
         */
        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        /**
         * Set current device
         *
         * @param device current device
         * @return this
         */
        public Builder setDevice(Device device) {
            this.device = device;
            return this;
        }

        /**
         * Use last ack commit
         *
         * @param useLastAckCommit true/false
         * @return this
         */
        public Builder setUseLastAckCommit(boolean useLastAckCommit) {
            this.useLastAckCommit = useLastAckCommit;
            return this;
        }

        /**
         * Set current connector {@link IPaymentDeviceConnector}
         *
         * @param connector payment device connector
         * @return this
         */
        public Builder setConnector(IPaymentDeviceConnector connector) {
            this.connector = connector;
            return this;
        }

        /**
         * Set sync links
         *
         * @param syncLinks sync links
         * @return this
         */
        public Builder setSyncLinks(SyncLinks syncLinks) {
            this.syncLinks = syncLinks;
            return this;
        }

        /**
         * Set sync initiator
         *
         * @return this
         */
        public Builder setSyncInitiator(@SyncInitiator.Initiator String initiator) {
            this.initiator = initiator;
            return this;
        }

        public SyncRequest build() {
            return new SyncRequest(syncId, user, device, useLastAckCommit, connector, syncLinks, initiator);
        }
    }
}
