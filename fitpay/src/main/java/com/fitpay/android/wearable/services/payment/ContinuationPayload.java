package com.fitpay.android.wearable.services.payment;

import android.os.ParcelUuid;
import android.util.Log;

import com.fitpay.android.wearable.utils.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ssteveli on 1/26/16.
 */
public class ContinuationPayload {

    private final static String LOG_TAG = ContinuationPayload.class.getCanonicalName();

    private final HashMap<Integer, byte[]> data;
    private final ParcelUuid targetUuid;

    public ContinuationPayload(ParcelUuid targetUuid) {
        this.data = new HashMap<>();
        this.targetUuid = targetUuid;
    }

    public void processPacket(byte[] packet) throws IOException {
        if (packet == null || packet.length < 2) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet));
        try {
            int sortOrder = in.readUnsignedShort();
            byte[] packetData = new byte[packet.length-2];
            in.read(packetData, 0, packet.length-2);

            if (data.containsKey(sortOrder)) {
                Log.d(LOG_TAG, "received duplicate continuation packet #" + sortOrder);
            }

            Log.d(LOG_TAG, "received packet #" + sortOrder + ": [" + Hex.bytesToHexString(packetData) + "]");
            data.put(sortOrder, packetData);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public byte[] getValue() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int x=0; x < data.size(); x++) {
            if (!data.containsKey(x)) {
                throw new IllegalStateException("invalid continuation payload, missing packet #" + x);
            }

            out.write(data.get(x));
        }

        return out.toByteArray();
    }

    public ParcelUuid getTargetUuid() {
        return targetUuid;
    }
}
