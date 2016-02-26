package com.fitpay.android.api.models;

//TODO: can we use Devices instead of this?
public class DeviceRef extends BaseModel{

    private String deviceType;
    private String deviceIdentifier;
    private String manufacturerName;
    private String deviceName;
    private String modelNumber;
    private String firmwareRevision;
    private String softwareRevision;
    private String createdTs;
    private long createdTsEpoch;
    private String externalReferenceId;

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public void setSoftwareRevision(String softwareRevision) {
        this.softwareRevision = softwareRevision;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    public String getSoftwareRevision() {
        return softwareRevision;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }
}
