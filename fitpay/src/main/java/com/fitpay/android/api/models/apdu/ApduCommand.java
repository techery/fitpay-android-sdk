package com.fitpay.android.api.models.apdu;

import com.fitpay.android.utils.Hex;

/**
 * Apdu command
 */
public final class ApduCommand {

    private String commandId;
    private int groupId;
    private int sequence;
    private String command;
    private String type;
    private boolean injected;
    private boolean continueOnFailure;

    // parent (owner) details
    private String commitId;
    private ApduPackage apduPackage;

    private ApduCommand() {
    }

    public String getCommandId() {
        return commandId;
    }

    public int getSequence() {
        return sequence;
    }

    public byte[] getCommand() {
        return Hex.hexStringToBytes(command);
    }

    public String getType() {
        return type;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isInjected() {
        return injected;
    }

    public boolean isContinueOnFailure() {
        return continueOnFailure;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public ApduPackage getApduPackage() {
        return apduPackage;
    }

    public void setApduPackage(ApduPackage apduPackage) {
        this.apduPackage = apduPackage;
    }

    public static class Builder {
        private String commandId;
        private int groupId;
        private int sequence;
        private String command;
        private String type;
        private boolean injected;
        private boolean continueOnFailure;

        public Builder() {
        }

        public ApduCommand build() {
            ApduCommand command = new ApduCommand();
            command.commandId = this.commandId;
            command.groupId = this.groupId;
            command.sequence = this.sequence;
            command.command = this.command;
            command.type = this.type;
            command.injected = this.injected;
            command.continueOnFailure = this.continueOnFailure;
            return command;
        }

        public Builder setCommandId(String commandId) {
            this.commandId = commandId;
            return this;
        }

        public Builder setGroupId(int groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder setSequence(int sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder setCommand(String command) {
            this.command = command;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setInjected(boolean injected) {
            this.injected = injected;
            return this;
        }

        public Builder setContinueOnFailure(boolean continueOnFailure) {
            this.continueOnFailure = continueOnFailure;
            return this;
        }
    }

    @Override
    public String toString() {
        return "ApduCommand{" +
                "commandId='" + commandId + '\'' +
                ", groupId=" + groupId +
                ", sequence=" + sequence +
                ", command='" + command + '\'' +
                ", type='" + type + '\'' +
                ", injected=" + injected +
                ", continueOnFailure=" + continueOnFailure +
                ", commitId='" + commitId + '\'' +
                ", apduPackage=" + apduPackage +
                '}';
    }
}
