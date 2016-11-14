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
}
