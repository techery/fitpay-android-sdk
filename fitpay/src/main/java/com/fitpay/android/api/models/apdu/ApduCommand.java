package com.fitpay.android.api.models.apdu;

import com.fitpay.android.utils.Hex;

/**
 * Apdu command
 */
public final class ApduCommand {

    private String commandId;
    private int sequence;
    private String command;
    private String type;
    private String description;

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

    public String getDescription() {
        return description;
    }
}
