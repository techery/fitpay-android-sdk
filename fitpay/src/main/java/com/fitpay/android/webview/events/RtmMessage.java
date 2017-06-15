package com.fitpay.android.webview.events;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.webview.enums.RtmType;

/**
 * RTM message from JS
 */
public class RtmMessage {

    private String jsonData;
    private String type;
    private String callbackId;

    public RtmMessage(String callbackId, Object data, String type) {
        this.callbackId = callbackId;
        this.type = type;
        if (data != null) {
            if (data instanceof String) {
                this.jsonData = (String) data;
            } else {
                jsonData = Constants.getGson().toJson(data);
            }
        }
    }

    public String getJsonData() {
        return jsonData;
    }

    @RtmType.Request
    public String getType() {
        return type;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public String toString() {
        return Constants.getGson().toJson(this);
    }
}
