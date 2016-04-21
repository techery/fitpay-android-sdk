package com.fitpay.android;

import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


/**
 * Created by tgs on 4/21/16.
 */
public class CreditCardTest2 extends TestActions {

    private User user;

    @Before
    public void setup() throws Exception {
        userName = getRandomLengthString(5, 10) + "@" + getRandomLengthString(5, 10) + "." + getRandomLengthString(4, 10);
        pin = getRandomLengthNumber(4, 4);

        loginIdentity = getTestLoginIdentity(userName, pin);
        doLogin(loginIdentity);

        this.user = getUser();
        assertNotNull(user);

    }

    @After
    public void deleteUser() throws Exception {
        if (null != this.user) {
            final CountDownLatch latch = new CountDownLatch(1);
            this.user.deleteUser(getSuccessDeterminingCallback(latch));
            latch.await(TIMEOUT, TimeUnit.SECONDS);
        }
    }

    @Test
    @Ignore //TODO complete writing the test
    public void testCanAddCreditCard() throws Exception {

        Device device = getTestDevice();
        Device createdDevice = createDevice(user, device);
        assertNotNull("created device", createdDevice);

        String pan = "5454545454545454";
        CreditCard creditCard = getTestCreditCard(pan);

        final CountDownLatch latch = new CountDownLatch(1);
        CreditCardProvidingCallback callback = new CreditCardProvidingCallback(latch);
        user.createCreditCard(creditCard, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        CreditCard createdCard = callback.getCreditCard();

        assertNotNull("created card",createdCard);
    }

    @Test
    public void testCantAddCreditCardWithNoDevice() throws Exception {
        String pan = "5454545454545454";
        CreditCard creditCard = getTestCreditCard(pan);

        final CountDownLatch latch = new CountDownLatch(1);
        CreditCardProvidingCallback callback = new CreditCardProvidingCallback(latch);
        user.createCreditCard(creditCard, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        CreditCard createdCard = callback.getCreditCard();

        assertNull("created card",createdCard);
        assertEquals("error code", 400, callback.getErrorCode());
    }




}
