package com.fitpay.android.paymentdevice.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.paymentdevice.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

/**
 * Data sync states enum
 */
public final class Sync {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.STARTED, States.IN_PROGRESS, States.COMPLETED, States.FAILED, States.SKIPPED, States.INC_PROGRESS, States.COMMIT_COMPLETED, States.COMPLETED_NO_UPDATES})
    public @interface State {
    }

    @State
    private final int state;
    private final String syncId;
    private final int value;
    private final String message;
    private final String syncEventId = UUID.randomUUID().toString();

    private Sync(int state, String syncId, int value, String message) {
        this.state = state;
        this.syncId = syncId;
        this.value = value;
        this.message = message;
    }

    /**
     * @deprecated  switch to new builder please :)
     *
     * @param syncId
     * @param state
     */
    public Sync(String syncId, @State int state) {
        this.state = state;
        this.syncId = syncId;
        this.message = null;
        this.value = -1;
    }

    /**
     * @deprecated  switch to new builder please :)
     *
     * @param syncId
     * @param state
     */
    public Sync(String syncId, @State int state, int value) {
        this.state = state;
        this.value = value;
        this.syncId = syncId;
        this.message = null;
    }

    /**
     * @deprecated  switch to new builder please :)
     *
     * @param syncId
     * @param state
     */
    public Sync(String syncId, @State int state, String message) {
        this.state = state;
        this.message = message;
        this.syncId = syncId;
        this.value = -1;
    }

    @Sync.State
    public int getState() {
        return state;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSyncId() {
        return syncId;
    }

    public String getSyncEventId() {
        return syncEventId;
    }

    @Override
    public String toString() {
        return "Sync{" +
                "state=" + state +
                ", syncId='" + syncId + '\'' +
                ", value=" + value +
                ", message='" + message + '\'' +
                ", syncEventId='" + syncEventId + '\'' +
                '}';
    }

    public static class Builder {
        @State
        private int state;
        private String syncId;
        private int value;
        private String message;

        public Builder state(int state) {
            this.state = state;
            return this;
        }

        public Builder syncId(String syncId) {
            this.syncId = syncId;
            return this;
        }

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Sync build() {
            return new Sync(state, syncId, value, message);
        }
    }
}
