package com.fitpay.android.api.models.card;

import com.fitpay.android.api.models.apdu.ApduCommand;

import java.util.List;

/**
 * Top of Wallet APDUs
 */
public class TopOfWallet {
    private List<ApduCommand> apduCommands;

    public List<ApduCommand> getApduCommands(){
        return apduCommands;
    }
}
