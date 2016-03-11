package com.fitpay.android.api.models;

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