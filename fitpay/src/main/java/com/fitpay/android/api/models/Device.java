package com.fitpay.android.api.models;


import android.support.annotation.NonNull;

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

    private Long createdTsEpoch;

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

    private List<CreditCard> cardRelationships;

    private String hostDeviceId;

    private Device(){}

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getModelNumber() {
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

    public String getLicenseKey() {
        return licenseKey;
    }

    public String getBdAddress() {
        return bdAddress;
    }

    public String getPairingTs() {
        return pairingTs;
    }

    public String getSecureElementId() {
        return secureElementId;
    }

    public List<CreditCard> getCardRelationships() {
        return cardRelationships;
    }

    public String getHostDeviceId() {
        return hostDeviceId;
    }

    public static final class Builder{

        private String deviceType;
        private String manufacturerName;
        private String deviceName;
        private String serialNumber;
        private String modelNumber;
        private String hardwareRevision;
        private String firmwareRevision;
        private String softwareRevision;
        private String systemId;
        private String osName;
        private String licenseKey;
        private String bdAddress;
        private String secureElementId;
        private String pairingTs;

        /**
         * Creates a Builder instance that can be used to build Gson with various configuration
         * settings. Builder follows the builder pattern, and it is typically used by first
         * invoking various configuration methods to set desired options, and finally calling
         * {@link #create()}.
         */
        public Builder(){
        }

        /**
         * Creates a {@link Device} instance based on the current configuration. This method is free of
         * side-effects to this {@code Builder} instance and hence can be called multiple times.
         *
         * @return an instance of {@link CreditCard} configured with the options currently set in this builder
         */
        public Device create(){
            Device device = new Device();
            device.deviceType = deviceType;
            device.manufacturerName = manufacturerName;
            device.deviceName = deviceName;
            device.serialNumber = serialNumber;
            device.modelNumber = modelNumber;
            device.hardwareRevision = hardwareRevision;
            device.firmwareRevision = firmwareRevision;
            device.softwareRevision = softwareRevision;
            device.systemId = systemId;
            device.osName = osName;
            device.licenseKey = licenseKey;
            device.bdAddress = bdAddress;
            device.secureElementId = secureElementId;
            device.pairingTs = pairingTs;
            return device;
        }

        /**
         * Set device name
         * @param deviceName The name of the device model.
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setDeviceName(@NonNull String deviceName){
            this.deviceName = deviceName;
            return this;
        }

        /**
         * Set device type
         * @param deviceType The type of device (PHONE, TABLET, ACTIVITY_TRACKER, SMARTWATCH, PC, CARD_EMULATOR, CLOTHING, JEWELRY, OTHER
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setDeviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        /**
         * Set manufacture name
         * @param manufacturerName The manufacturer name of the device.
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setManufacturerName(String manufacturerName) {
            this.manufacturerName = manufacturerName;
            return this;
        }

        /**
         * Set serial number
         * @param serialNumber The serial number for a particular instance of the device
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        /** Set model number
         * @param modelNumber The model number that is assigned by the device vendor.
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setModelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        /**
         * Set hardware revision
         * @param hardwareRevision The hardware revision for the hardware within the device.
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setHardwareRevision(String hardwareRevision) {
            this.hardwareRevision = hardwareRevision;
            return this;
        }

        /**
         * Set firmware revision
         * @param firmwareRevision The firmware revision for the hardware within the device.
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setFirmwareRevision(String firmwareRevision) {
            this.firmwareRevision = firmwareRevision;
            return this;
        }

        /**
         * Set software revision
         * @param softwareRevision The software revision for the hardware within the device.
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setSoftwareRevision(String softwareRevision) {
            this.softwareRevision = softwareRevision;
            return this;
        }

        /**
         * Set system ID
         * @param systemId A structure containing an Organizationally Unique Identifier (OUI)
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setSystemId(String systemId) {
            this.systemId = systemId;
            return this;
        }

        /**
         * Set OS name
         * @param osName The name of the operating system
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setOSName(String osName) {
            this.osName = osName;
            return this;
        }

        /**
         * Set license key
         * @param licenseKey The license key parameter is used to read or write the license key of the device
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setLicenseKey(String licenseKey) {
            this.licenseKey = licenseKey;
            return this;
        }

        /**
         * Set bluetooth device address
         * @param bdAddress The BD address parameter is used to read the Bluetooth device address
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setBdAddress(String bdAddress) {
            this.bdAddress = bdAddress;
            return this;
        }

        /**
         * Set secure element id
         * @param secureElementId The ID of a secure element in a payment capable device
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setSecureElementId(String secureElementId) {
            this.secureElementId = secureElementId;
            return this;
        }

        /**
         * Set pairing time
         * @param pairingTs The time the device was paired
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setPairingTs(String pairingTs) {
            this.pairingTs = pairingTs;
            return this;
        }
    }
}