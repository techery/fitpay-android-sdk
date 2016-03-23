package com.fitpay.android.wearable.services.deviceinfo;

/**
 * Created by tgs on 3/18/16.
 */
public interface DeviceInfoProvider {

    String getManufacturerName();
    String getModelNumber();
    String getSerialNumber();
    String getFirmwareRevisionNumber();
    String getHardwareRevisionNumber();
    String getSoftwareRevisionNumber();
    String getSystemId();

}
