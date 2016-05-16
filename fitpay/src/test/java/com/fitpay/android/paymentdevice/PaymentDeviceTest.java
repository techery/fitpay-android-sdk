package com.fitpay.android.paymentdevice;

import android.content.Context;
import android.util.Log;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.callbacks.PaymentDeviceListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.impl.mock.MockPaymentDeviceService;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;
import com.fitpay.android.utils.NotificationManager;
import com.orhanobut.logger.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;

/**
 * Created by tgs on 5/3/16.
 */
@RunWith(RobolectricTestRunner.class)
public class PaymentDeviceTest {

    private final static String TAG = PaymentDeviceTest.class.getSimpleName();

    protected IPaymentDeviceService paymentDeviceService;
    protected long delay = 10000;

    private NotificationManager manager;


    Context context;

    @BeforeClass
    public static void init() {
        Logger.init();
    }

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;

        context = RuntimeEnvironment.application.getApplicationContext();
        paymentDeviceService = new MockPaymentDeviceService();
        manager = NotificationManager.getInstance();
    }


    @Test
    @Ignore  // subscription is not correct - looks like events are not on main thread
    public void canConnect() {
        final int[] connectionStates = { paymentDeviceService.getState() };
        assertEquals("payment service is not initialized", States.INITIALIZED, paymentDeviceService.getState());


        PaymentDeviceListener listener = new PaymentDeviceListener() {
            @Override
            public void onDeviceStateChanged(@Connection.State int state) {
                Log.d(TAG, "status changed.  new status: " + state);
                connectionStates[0] = state;
            }

            @Override
            public void onDeviceInfoReceived(Device device) {
                // do nothing
            }

            @Override
            public void onNFCStateReceived(boolean isEnabled, byte errorCode) {
                // do nothing
            }

            @Override
            public void onNotificationReceived(byte[] data) {
                // do nothing
            }

            @Override
            public void onApplicationControlReceived(byte[] data) {
                // do nothing
            }
        };

        manager.addListener(listener);

        paymentDeviceService.connect();

        delay(20000);
        assertEquals("payment service is not connected", States.CONNECTED, paymentDeviceService.getState());
        assertEquals("connection state as captured by listener", States.CONNECTED, connectionStates[0]);
    }

    @Test
    @Ignore  // subscription is not correct - looks like events are not on main thread
    public void canReadDeviceInfo() {
        //TODO replace with async
        if (paymentDeviceService instanceof MockPaymentDeviceService) {
//            MockPaymentDeviceService mock = (MockPaymentDeviceService) paymentDeviceService;
//            assertNull("device info should not be available", mock.getDevice());
//            paymentDeviceService.readDeviceInfo();
//            delay(5000);
//            assertNotNull("device info should be available", mock.getDevice());
        }
    }

    protected void delay(long value){
        try {
            Thread.sleep(value);
        } catch (InterruptedException e) {
            // proceed
        }
    }

}
