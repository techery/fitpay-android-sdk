package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.interfaces.IControlMessage;
import com.fitpay.android.wearable.interfaces.INotificationMessage;
import com.fitpay.android.wearable.interfaces.ISecureMessage;

/**
 * Wearable callbacks
 */
public abstract class WearableListener extends ConnectionListener implements IListeners.WearableListener {
    public WearableListener() {
        super();
        mCommands.put(Device.class, data -> onDeviceInfoReceived((Device) data));
        mCommands.put(IControlMessage.class, data -> {
            IControlMessage message = (IControlMessage) data;
            onApplicationControlReceived(message.getData());
        });
        mCommands.put(INotificationMessage.class, data -> {
            INotificationMessage message = (INotificationMessage) data;
            onNotificationReceived(message.getData());
        });
        mCommands.put(ISecureMessage.class, data -> {
            ISecureMessage message = (ISecureMessage) data;
            onNFCStateReceived(message.isNfcEnabled(), message.getNfcErrorCode());
        });
    }
}
