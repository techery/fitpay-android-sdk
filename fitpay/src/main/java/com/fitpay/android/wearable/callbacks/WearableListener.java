package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.Connection;
import com.fitpay.android.wearable.interfaces.IControlMessage;
import com.fitpay.android.wearable.interfaces.ITransactionMessage;
import com.fitpay.android.wearable.interfaces.ISecureMessage;

/**
 * Created by Vlad on 05.04.2016.
 */
public abstract class WearableListener extends ApduListener implements IWearableListener {
    public WearableListener() {
        super();
        mCommands.put(Connection.class, data -> {
            Connection event = (Connection) data;
            onDeviceStateChanged(event.getState());
        });
        mCommands.put(Device.class, data -> onDeviceInfoReceived((Device) data));
        mCommands.put(IControlMessage.class, data -> {
            IControlMessage message = (IControlMessage) data;
            onApplicationControlReceived(message.getData());
        });
        mCommands.put(ITransactionMessage.class, data -> {
            ITransactionMessage message = (ITransactionMessage) data;
            onTransactionReceived(message.getData());
        });
        mCommands.put(ISecureMessage.class, data -> {
            ISecureMessage message = (ISecureMessage) data;
            onNFCStateReceived(message.isNfcEnabled(), message.getNfcErrorCode());
        });
    }
}
