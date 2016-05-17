package com.fitpay.android.paymentdevice.impl.pagare;

import android.util.Log;

import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.model.PaymentDeviceService;
import com.fitpay.android.utils.RxBus;
import com.getpebble.android.kit.PebbleKit;

import java.util.Properties;
import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tgs on 5/16/16.
 */
public class PebblePagarePaymentServiceDevice extends PaymentDeviceService {

    private final static String TAG = PebblePagarePaymentServiceDevice.class.getSimpleName();

    public static final String EXTRA_PEBBLE_APP_UUID = "PEBBLE_APP_UUID";

    private String pebbleAppUuid;
    private Device device;
    // for mock response delay
    private final int delay = 3000;
    private final Random random = new Random();


    public PebblePagarePaymentServiceDevice() {
        state = States.INITIALIZED;
        loadDefaultDevice();
    }

    @Override
    public void init(Properties props) {
        pebbleAppUuid = props.getProperty(EXTRA_PEBBLE_APP_UUID);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void connect() {
        boolean isConnected = PebbleKit.isWatchConnected(mContext);
        if (isConnected) {
            setState(States.CONNECTED);
        } else {
            setState(States.DISCONNECTED);
            //TODO need to fire some kind of event to inform client that connect failed
        }

    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void readDeviceInfo() {
        // Is the watch connected?
        boolean isConnected = PebbleKit.isWatchConnected(mContext);
        if (!isConnected) {
            //TODO need to fire some kind of event to inform client that connect and hence readDeviceInfo failed
            return;
        }

        Subscription subscription = getReadPebbleDeviceInfoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getPebbleDeviceInfoObserver());

        // Mock device read

        Subscription deviceReadSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getDeviceInfoObserver(device));

    }

    @Override
    public void readNFCState() {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void setNFCState(@NFC.Action byte state) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void executeApduPackage(ApduPackage apduPackage) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void sendNotification(byte[] data) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    @Override
    public void setSecureElementState(@SecureElement.Action byte state) {
        throw new UnsupportedOperationException("method not supported in this iteration");

    }

    private Observable<PebbleDeviceInfo> getReadPebbleDeviceInfoObservable() {
        return Observable.just(true).map(new Func1<Boolean, PebbleDeviceInfo>() {
            @Override
            public PebbleDeviceInfo call(Boolean aBoolean) {
                PebbleKit.FirmwareVersionInfo info = PebbleKit.getWatchFWVersion(mContext);
                boolean appMessageSupported = PebbleKit.areAppMessagesSupported(mContext);
                PebbleDeviceInfo pebble = new PebbleDeviceInfo.Builder()
                        .setFirmwareVersion(info.getMajor() + "." + info.getMinor() + "." + info.getPoint())
                        .setAppMessageSupported(appMessageSupported)
                        .build();
                return pebble;
            }
        });
    }


    private Observer<PebbleDeviceInfo> getPebbleDeviceInfoObserver() {

        return new Observer<PebbleDeviceInfo>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "pebble deviceInfo observer completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "pebble deviceInfo observer error: " + e.getMessage());
            }

            @Override
            public void onNext(PebbleDeviceInfo deviceInfo) {
                Log.d(TAG, "pebble device info has been read.  device: " + deviceInfo);
                if (null != deviceInfo) {
                    RxBus.getInstance().post(deviceInfo);
                } else {
                    Log.e(TAG, "pebble read device info returned null. This is a application defect");
                }
            }
        };
    }

    // Mock device info methods
    protected void loadDefaultDevice() {
        device = new Device.Builder()
                .setDeviceType(DeviceTypes.WATCH)
                .setManufacturerName("Fitpay")
                .setDeviceName("Pagare Smart Strap")
                .setSerialNumber("074DC456B5")
                .setModelNumber("Pagare One")
                .setHardwareRevision("1.0.0.0")
                .setFirmwareRevision("1030.6408.1309.0001")
                //.setSoftwareRevision("2.0.242009.6")
                .setSystemId("0x123456FFFE9ABCDE")
                .setOSName("ANDROID")
                .setLicenseKey("6b413f37-90a9-47ed-962d-80e6a3528036")
                //.setBdAddress("977214bf-d038-4077-bdf8-226b17d5958d")
                .setSecureElementId("8765b2c7-74c5-43e5-b224-39992060161b")
                .build();
    }

    private Observable<Boolean> getAsyncSimulatingObservable() {
        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                delay(delay);
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getDeviceInfoObserver(final Device deviceInfo) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "device info has been read.  device: " + deviceInfo);
                if (null != deviceInfo) {
                    RxBus.getInstance().post(deviceInfo);
                } else {
                    Log.e(TAG, "read device info returned null. This is a application defect");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "deviceInfo observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "deviceInfo observer onNext: " + bool);
            }
        };
    }



    protected void delay(long delayInterval) {

        try {
            Thread.sleep(random.nextInt((int)delayInterval));
        } catch (InterruptedException e) {
            // carry on
        }
    }


}

