package com.fitpay.android.api.models.apdu;

import com.fitpay.android.api.models.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

abstract class ApduPackageModel extends BaseModel {

    private String seIdType;
    private String targetDeviceType;
    private String targetDeviceId;
    private String seId;
    private String targetAid;
    private String validUntil;
    private String apduPackageUrl;
    @SerializedName("commandApdus")
    private List<ApduCommand> apduCommands;

    public String getSeIdType() {
        return seIdType;
    }

    public String getTargetDeviceType() {
        return targetDeviceType;
    }

    public String getTargetDeviceId() {
        return targetDeviceId;
    }

    public String getSeId() {
        return seId;
    }

    public String getTargetAid() {
        return targetAid;
    }

    public List<ApduCommand> getApduCommands() {
        return apduCommands;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public String getApduPackageUrl() {
        return apduPackageUrl;
    }
}
