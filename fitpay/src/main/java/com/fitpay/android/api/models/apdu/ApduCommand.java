package com.fitpay.android.api.models.apdu;

import com.fitpay.android.wearable.ble.utils.Hex;

/**
 * Created by Vlad on 01.04.2016.
 */
public final class ApduCommand {

    private String commandId;
    private int groupId;
    private int sequence;
    private String command;
    private String type;

    private ApduCommand(){}

    public String getCommandId() {
        return commandId;
    }

    public int getGroupId() {
        return groupId;
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
}
