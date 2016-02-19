package com.fitpay.android.api.models;


public class Relationship {

    private RelationshipCard card;
    private Device device;

    public RelationshipCard getCard() {
        return card;
    }

    public void setCard(RelationshipCard card) {
        this.card = card;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}