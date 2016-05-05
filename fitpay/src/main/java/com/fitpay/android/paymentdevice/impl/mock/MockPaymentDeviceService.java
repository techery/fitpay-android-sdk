package com.fitpay.android.paymentdevice.impl.mock;

import android.content.Context;

import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.model.PaymentDeviceService;
import com.fitpay.android.utils.RxBus;

import java.util.Random;

/**
 * Created by tgs on 5/3/16.
 */
public class MockPaymentDeviceService extends PaymentDeviceService {

    private Device device;
    private final Random random = new Random();
    private final int delay = 3000;
    private boolean deviceInfoRead = false;

    public MockPaymentDeviceService(Context context, String address) {
        super(context, address);
        loadDefaultDevice();
        state = States.INITIALIZED;
    }

    public MockPaymentDeviceService(Context context, String address, Device device) {
        this(context, address);
        this.device = device;
    }


    protected void loadDefaultDevice() {
        device = new Device.Builder()
            .setDeviceType(DeviceTypes.WATCH)
            .setManufacturerName("Fitpay")
                .setDeviceName("PSPS")
        .setSerialNumber("074DCC022E14")
        .setModelNumber("FB404")
        .setHardwareRevision("1.0.0.0")
        .setFirmwareRevision("1030.6408.1309.0001")
        .setSoftwareRevision("2.0.242009.6")
        .setSystemId("0x123456FFFE9ABCDE")
        .setOSName("ANDROID")
        .setLicenseKey("6b413f37-90a9-47ed-962d-80e6a3528036")
        .setBdAddress("977214bf-d038-4077-bdf8-226b17d5958d")
        .setSecureElementId("8615b2c7-74c5-43e5-b224-38882060161b")
                .create();
    }

    @Override
    public void close() {

    }

    @Override
    public void connect() {
        Thread thread = new Thread() {
            public void run() {
                delay();
                setState(States.CONNECTING);
                delay();
                setState(States.CONNECTED);
            }
        };
        thread.start();
    }

    protected void delay() {
        try {
            Thread.sleep(random.nextInt(delay));
        } catch (InterruptedException e) {
            // carry on
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void reconnect() {

    }

    @Override
    public void readDeviceInfo() {
        deviceInfoRead = true;
        Thread thread = new Thread() {
            public void run() {
                delay();
            }
        };
        thread.start();
        RxBus.getInstance().post(device);
    }

    @Override
    public void readNFCState() {

    }

    @Override
    public void setNFCState(@NFC.Action byte state) {

    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {

    }

    @Override
    public void sendNotification(byte[] data) {

    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {

    }

    //TODO replace this with callback provides value
    public Device getDevice() {
        if (deviceInfoRead) {
            return device;
        }
        return null;
    }
}
