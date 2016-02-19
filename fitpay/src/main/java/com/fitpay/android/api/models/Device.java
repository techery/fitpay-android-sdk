package com.fitpay.android.api.models;


import java.util.List;

public class Device extends BaseModel {

    /**
     * description : The type of device (PHONE, TABLET, ACTIVITY_TRACKER, SMARTWATCH, PC, CARD_EMULATOR, CLOTHING, JEWELRY, OTHER
     */
    private String deviceType;

    private String deviceIdentifier;

    /**
     * description : The manufacturer name of the device.
     */
    private String manufacturerName;

    /**
     * description : The name of the device model.
     */
    private String deviceName;

    /**
     * description : The serial number for a particular instance of the device
     */
    private String serialNumber;

    /**
     * description : The model number that is assigned by the device vendor.
     */
    private String modelNumber;

    /**
     * description : The hardware revision for the hardware within the device.
     */
    private String hardwareRevision;

    /**
     * description : The firmware revision for the firmware within the device.
     */
    private String firmwareRevision;

    /**
     * description : The software revision for the software within the device.
     */
    private String softwareRevision;

    private String createdTs;

    private long createdTsEpoch;

    /**
     * description : The name of the operating system
     */
    private String osName;

    /**
     * description : A structure containing an Organizationally Unique Identifier (OUI)
     * followed by a manufacturer-defined identifier and is unique for each individual instance of the product.
     */
    private String systemId;

    /**
     * description : The license key parameter is used to read or write the license key of the device
     */
    private String licenseKey;

    /**
     * description : The BD address parameter is used to read the Bluetooth device address
     */
    private String bdAddress;

    /**
     * description : The time the device was paired
     */
    private String pairingTs;

    /**
     * description : The ID of a secure element in a payment capable device
     */
    private String secureElementId;

    private List<CardRelationship> cardRelationships;

    private String hostDeviceId;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getAnufacturerName() {
        return manufacturerName;
    }

    public void setAnufacturerName(String anufacturerName) {
        this.manufacturerName = anufacturerName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOdelNumber() {
        return modelNumber;
    }

    public void setOdelNumber(String odelNumber) {
        this.modelNumber = odelNumber;
    }

    public String getHardwareRevision() {
        return hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public String getSoftwareRevision() {
        return softwareRevision;
    }

    public void setSoftwareRevision(String softwareRevision) {
        this.softwareRevision = softwareRevision;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getBdAddress() {
        return bdAddress;
    }

    public void setBdAddress(String bdAddress) {
        this.bdAddress = bdAddress;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSecureElementId() {
        return secureElementId;
    }

    public void setSecureElementId(String secureElementId) {
        this.secureElementId = secureElementId;
    }

    public String getPairingTs() {
        return pairingTs;
    }

    public void setPairingTs(String pairingTs) {
        this.pairingTs = pairingTs;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public List<CardRelationship> getCardRelationships() {
        return cardRelationships;
    }

    public void setCardRelationships(List<CardRelationship> cardRelationships) {
        this.cardRelationships = cardRelationships;
    }

    public String getHostDeviceId() {
        return hostDeviceId;
    }

    public void setHostDeviceId(String hostDeviceId) {
        this.hostDeviceId = hostDeviceId;
    }
}