package com.fitpay.android.wearable.message;

import com.fitpay.android.wearable.utils.Hex;

/**
 * Created by tgs on 3/23/16.
 */
public class ContinuationControlMessageFactory {

    private ContinuationControlMessageFactory() {
        // static methods only
    }

    public static ContinuationControlMessage withMessage(byte[] message) {

        if (null == message) {
            return null;
        }

        if (message.length < 1) {
            throw new IllegalArgumentException("invalid continuation message: " + Hex.bytesToHexString(message));
        }

        if (message[0] == 0x00) {
            ContinuationControlBeginMessage continuationMessage = new ContinuationControlBeginMessage();
            return continuationMessage.withMessage(message);
        } else if (message[0] == 0x01) {
            ContinuationControlEndMessage continuationMessage = new ContinuationControlEndMessage();
            return continuationMessage.withMessage(message);
        } else {
            throw new IllegalArgumentException("invalid continuation message: " + Hex.bytesToHexString(message));
        }
    }
}
