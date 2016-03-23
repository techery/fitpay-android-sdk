package com.fitpay.android.wearable.services.payment;

import android.util.Log;

import com.fitpay.android.wearable.utils.Hex;

import java.util.Arrays;
import java.util.Random;

/**
 * This class represents a simulated secure element, it's primary job is to simulate the execution
 * of APDU commands
 */
public class SecureElement {

    private final static String LOG_TAG = SecureElement.class.getCanonicalName();

    public final static byte[] SUCCESS = new byte[] { (byte)0x90, (byte)0x00 };
    public final static byte[] INVALID_REQUEST = new byte[] { (byte)0x91, (byte)0x7e };

    private final static Random random = new Random(System.currentTimeMillis());
    private final String id;

    private static SecureElement secureElement;

    public synchronized static SecureElement getInstance() {
        if (secureElement == null) {
            secureElement = new SecureElement();
        }

        return secureElement;
    }

    private SecureElement() {
        this.id = getRandomString(16);
    }

    public String getId() {
        return id;
    }

    private static String getRandomString(int length) {
        final String chars = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(chars.length());
            sb.append(chars.substring(pos, pos+1));
        }
        return sb.toString();
    }

    /**
     * Handles APDU commands, providing for a few simulated use cases.
     *
     * 1. commands that are empty will generate a 917E response
     * 2. commands that start with 99, the next two bytes will be the response code returned (i.e.
     * 999101 would result in a 9101 response)
     * 3. commands that start with 98: the payload will be returned as the data and the next 2 bytes
     * will be the response code. Example: message 989101012345678900 will result in 9891010123456789009101.
     * This can be used to generate continuation responses
     *
     * Reference: https://www.eftlab.com.au/index.php/site-map/knowledge-base/118-apdu-response-list
     *
     * @param request
     * @return
     */
    public byte[] process(byte[] request) {
        Log.d(LOG_TAG, "getting response to apdu: [" + Hex.bytesToHexString(request) + "]");
        byte[] returnValue = null;
        // add some random latency
        try {
            int latency = 2000 + random.nextInt(500);
            Log.d(LOG_TAG, "mock secure element is processing");
            Thread.sleep(latency);
            Log.d(LOG_TAG, "mock secure element done processing");
        } catch (InterruptedException e) {
            // ignore, just return
        }

        if (request == null || request.length == 0) {
            return INVALID_REQUEST;
        }

        // should we simulate an error?  if the first byte is 0x99 or 0x98, then the
        // next two represent the simulated error
        if (request[0] == (byte)0x99) {
            return new byte[] { request[1], request[2] };
        }

        if (request[0] == (byte)0x98) {
            byte[] val = new byte[request.length + 2];
            System.arraycopy(request, 0, val, 0, request.length);
            System.arraycopy(new byte[] { request[1], request[2] }, 0, val, request.length, 2);
            return val;
        }

        // if we get here, it's a simulated success
        return SUCCESS;
    }

    /**
     * Utility method to evaluate a secure element repsonse and determine if was successful or
     * not.
     *
     * @param response
     * @return
     */
    public boolean wasSuccessful(byte[] response) {
        if (response == null || response.length < 2) {
            return false;
        }

        byte[] r = new byte[2];
        System.arraycopy(response, response.length - 2, r, 0, 2);

        if (Arrays.equals(r, SUCCESS)) {
            return true;
        }

        return false;
    }
}
