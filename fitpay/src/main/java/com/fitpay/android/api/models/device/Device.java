package com.fitpay.android.api.models.device;


import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.CreditCardRef;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.utils.TimestampUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Device extends DeviceModel {

    private static final String COMMITS = "commits";

    private List<CreditCardRef> cardRelationships;

    public List<CreditCardRef> getCardRelationships() {
        return cardRelationships;
    }

    /**
     * Update the details of an existing device.
     *
     * @param callback   result callback
     */
    public void updateDevice(@NonNull Device device, @NonNull ApiCallback<Device> callback){
        makePatchCall(device, false, Device.class, callback);
    }

    /**
     * Delete a single device.
     *
     * @param callback result callback
     */
    public void deleteDevice(@NonNull ApiCallback<Void> callback){
        makeDeleteCall(callback);
    }

    /**
     * Retrieves a collection of all events that should be committed to this device.
     *
     * @param limit        Max number of events per page, default: 10
     * @param offset       Start index position for list of entities returned
     * @param callback     result callback
     */
    public void getCommits(int limit, int offset, final ApiCallback<Collections.CommitsCollection> callback) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("limit", limit);
        queryMap.put("offset", offset);
        makeGetCall(COMMITS, queryMap, Collections.CommitsCollection.class, callback);
    }

    public static final class Builder{
        @DeviceTypes.Type
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
            device.secureElement = new SecureElement(secureElementId);
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
        public Builder setDeviceType(@DeviceTypes.Type String deviceType) {
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
        public Builder setPairingTs(long pairingTs) {
            this.pairingTs = TimestampUtils.getISO8601StringForTime(pairingTs);
            return this;
        }
    }
}