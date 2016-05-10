package com.fitpay.android.paymentdevice.impl.mock;

import android.content.Context;
import android.util.Log;

import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;
import com.fitpay.android.paymentdevice.model.PaymentDeviceService;
import com.fitpay.android.utils.RxBus;

import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tgs on 5/3/16.
 */
public class MockPaymentDeviceService extends PaymentDeviceService {

    private final String TAG = MockPaymentDeviceService.class.getSimpleName();

    private Device device;
    private final Random random = new Random();
    private final int delay = 3000;
    private boolean deviceInfoRead = false;

    Subscription connectionSubscription;
    Subscription deviceReadSubscription;

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
        Log.d(TAG, "close not implemented");
    }

    @Override
    public void connect() {

        Log.d(TAG, "payment device connect requested.   current state: " + getState());

        if (getState() == States.CONNECTED) {
            return;
        }

        connectionSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getConnectionObserver(States.CONNECTING));
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

    private Observer<Boolean> getConnectionObserver(@Connection.State int targetState) {

        return new Observer<Boolean>() {

            private final int newState = targetState;

            @Override
            public void onCompleted() {
                Log.d(TAG, "connection changed state.  new state: " + newState);
                setState(newState);
                if (newState == States.CONNECTING) {
                    connectionSubscription = getAsyncSimulatingObservable()//
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe(getConnectionObserver(States.CONNECTED));
                } else if (newState == States.DISCONNECTING) {
                    connectionSubscription = getAsyncSimulatingObservable()//
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe(getConnectionObserver(States.DISCONNECTED));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "connection observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "connection observer onNext: " + bool);
            }
        };
    }


    private Observer<Boolean> getDeviceInfoObserver(final Device deviceInfo) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "device info has been read.  device: " + deviceInfo);
                RxBus.getInstance().post(device);
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

    @Override
    public void disconnect() {
        Log.d(TAG, "payment device disconnect requested.  current state: " + getState());

        connectionSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getConnectionObserver(States.DISCONNECTING));
    }

    @Override
    public void readDeviceInfo() {

        Log.d(TAG, "payment device readDeviceInfo requested");

        deviceReadSubscription = getAsyncSimulatingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getDeviceInfoObserver(device));
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
}
