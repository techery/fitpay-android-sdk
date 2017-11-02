package com.fitpay.android.paymentdevice.utils;

import android.util.Log;

import com.fitpay.android.paymentdevice.impl.PaymentDeviceConnector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manager of {@link PaymentDeviceConnector}
 */
public class PaymentDeviceConnectorManager {

    private static String TAG = PaymentDeviceConnector.class.getName();

    private static PaymentDeviceConnectorManager sInstance = null;

    private Map<String, PaymentDeviceConnector> connectors = new HashMap<>();

    private PaymentDeviceConnectorManager() {
    }

    public static PaymentDeviceConnectorManager getInstance() {
        if (null == sInstance) {
            sInstance = new PaymentDeviceConnectorManager();
        }

        return sInstance;
    }

    public PaymentDeviceConnector getConnector(String id) {
        return connectors.get(id);
    }

    /**
     * add new connector
     *
     * @param id        string value to determine your connector
     * @param connector payment device connector
     */
    public void addConnector(String id, PaymentDeviceConnector connector) {
        for (PaymentDeviceConnector value : connectors.values()) {
            if (value == connector) {
                Log.e(TAG, "connector has been added already");
                return;
            }
        }

        if (connectors.containsKey(id)) {
            Log.e(TAG, "id:" + id + " already exist");

            if (connectors.get(id) != connector) {
                Log.e(TAG, "id:" + id + " uses another connector");
            }

            return;
        }

        connectors.put(id, connector);
    }

    /**
     * remove connector by id from the list
     *
     * @param id
     */
    public void removeConnector(String id) {
        connectors.remove(id);
    }

    /**
     * remove connector by value from the list
     *
     * @param connector
     */
    public void removeConnector(PaymentDeviceConnector connector) {
        for (Iterator<Map.Entry<String, PaymentDeviceConnector>> it = connectors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PaymentDeviceConnector> entry = it.next();
            if (entry.getValue() == connector) {
                it.remove();
                return;
            }
        }
    }
}
