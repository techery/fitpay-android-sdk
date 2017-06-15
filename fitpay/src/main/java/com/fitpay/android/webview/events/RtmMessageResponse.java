package com.fitpay.android.webview.events;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.webview.enums.RtmType;

/**
 * Created by Vlad on 02.11.2016.
 */

public class RtmMessageResponse {

    private Boolean isSuccess;
    private Object data;
    @RtmType.Response
    private String type;
    private String callBackId;

    public RtmMessageResponse(String type) {
        this(null, null, type);
    }

    public RtmMessageResponse(Object data, @RtmType.Response String type) {
        this(null, data, type);
    }

    public RtmMessageResponse(String callBackId, Object data, @RtmType.Response String type) {
        this(callBackId, null, data, type);
    }

    public RtmMessageResponse(String callBackId, Boolean success, Object data, @RtmType.Response String type) {
        this.callBackId = callBackId;
        this.isSuccess = success;
        this.type = type;
        this.data = data;
    }

    public String toString() {
        return Constants.getGson().toJson(this);
    }
}
