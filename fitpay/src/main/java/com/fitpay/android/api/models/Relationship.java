package com.fitpay.android.api.models;

import com.fitpay.android.api.models.device.Device;

public final class Relationship extends BaseModel {

    //TODO: add @Card

    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}