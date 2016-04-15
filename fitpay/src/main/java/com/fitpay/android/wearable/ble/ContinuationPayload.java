package com.fitpay.android.wearable.ble;

import com.fitpay.android.wearable.utils.Hex;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ssteveli on 1/26/16.
 */
class ContinuationPayload {

    private final HashMap<Integer, byte[]> data;
    private final UUID targetUuid;

    public ContinuationPayload(UUID targetUuid) {
        this.data = new HashMap<>();
        this.targetUuid = targetUuid;
    }

    public void processPacket(ContinuationPacketMessage message) throws IOException {
        if (message == null) {
            return;
        }

        if (data.containsKey(message.getSortOrder())) {
            Logger.d("received duplicate continuation packet #" + message.getSortOrder());
        }

        Logger.d("received packet #" + message.getSortOrder() + ": [" + Hex.bytesToHexString(message.getData()) + "]");
        data.put(message.getSortOrder(), message.getData());
    }

    public byte[] getValue() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int x = 0; x < data.size(); x++) {
            if (!data.containsKey(x)) {
                throw new IllegalStateException("invalid continuation payload, missing packet #" + x);
            }

            out.write(data.get(x));
        }

        return out.toByteArray();
    }

    public UUID getTargetUuid() {
        return targetUuid;
    }
}
