package com.fitpay.android.paymentdevice;

import android.content.Context;

import com.fitpay.android.paymentdevice.callbacks.ConnectionListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.impl.mock.MockPaymentDeviceService;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;
import com.orhanobut.logger.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by tgs on 5/3/16.
 */
@RunWith(RobolectricTestRunner.class)
public class PaymentDeviceTest {

    protected IPaymentDeviceService paymentDeviceService;
    protected long delay = 10000;

    Context context;

    @BeforeClass
    public static void init() {
        Logger.init();
    }

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        paymentDeviceService = new MockPaymentDeviceService(context, "mockAddress");
    }


    @Test
    public void canConnect() {
        final int[] connectionStates = { paymentDeviceService.getState() };
        assertEquals("payment service is not initialized", States.INITIALIZED, paymentDeviceService.getState());
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void onDeviceStateChanged(@Connection.State int state) {
                connectionStates[0] = state;
            }
        };
        paymentDeviceService.connect();
        delay(8000);
        assertEquals("payment service is not connected", States.CONNECTED, paymentDeviceService.getState());
    }

    @Test
    public void canReadDeviceInfo() {
        //TODO replace with async get when implemented
        if (paymentDeviceService instanceof MockPaymentDeviceService) {
            MockPaymentDeviceService mock = (MockPaymentDeviceService) paymentDeviceService;
            assertNull("device info should not be available", mock.getDevice());
            paymentDeviceService.readDeviceInfo();
            delay(5000);
            assertNotNull("device info should be available", mock.getDevice());
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
