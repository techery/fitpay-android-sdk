package com.fitpay.android;

import com.fitpay.android.api.ApiManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Test constants
 */
public final class TestConstants {

    static Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(ApiManager.PROPERTY_API_BASE_URL, System.getProperty(ApiManager.PROPERTY_API_BASE_URL, "https://api.fit-pay.com"));
        config.put(ApiManager.PROPERTY_AUTH_BASE_URL, System.getProperty(ApiManager.PROPERTY_AUTH_BASE_URL, "https://auth.fit-pay.com"));
        config.put(ApiManager.PROPERTY_CLIENT_ID, System.getProperty(ApiManager.PROPERTY_CLIENT_ID, "fp_webapp_pJkVp2Rl"));
        config.put(ApiManager.PROPERTY_REDIRECT_URI, System.getProperty(ApiManager.PROPERTY_REDIRECT_URI, "https://webapp.fit-pay.com"));

        System.out.println("test configuration: " + config);

        return config;
    }

    static void waitSomeActionsOnServer() throws InterruptedException {
        Thread.sleep(1000);
    }
}
