package com.fitpay.android.models;


public class Device {

    private String deviceType;
    private String deviceIdentifier;
    private String manufacturerName;
    private String deviceName;
    private String serialNumber;
    private String modelNumber;
    private String hardwareRevision;
    private String firmwareRevision;
    private String softwareRevision;
    private String createdTs;
    private long createdTsEpoch;
    private String osName;
    private String systemId;

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public void setAnufacturerName(String anufacturerName) {
        this.manufacturerName = anufacturerName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setOdelNumber(String odelNumber) {
        this.modelNumber = odelNumber;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
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

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public String getAnufacturerName() {
        return manufacturerName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getOdelNumber() {
        return modelNumber;
    }

    public String getHardwareRevision() {
        return hardwareRevision;
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

    public String getOsName() {
        return osName;
    }

    public String getSystemId() {
        return systemId;
    }

}