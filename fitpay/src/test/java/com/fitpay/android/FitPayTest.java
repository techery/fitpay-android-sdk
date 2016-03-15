package com.fitpay.android;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.LoginIdentity;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.utils.ApiManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.JVM)
public class FitPayTest {

    private static Collections.CreditCardCollection cardsCollection;
    private static CountDownLatch latch = new CountDownLatch(1);
    private static User currentUser;
    private static boolean isRequestSuccess = false;

    private final LoginIdentity loginIdentity = new LoginIdentity.Builder()
//                .setUsername("skynet17@ya.ru")
            .setUsername("test@test.test")
            .setPassword("1221")
            .setClientId("pagare")
            .setRedirectUri("https://demo.pagare.me")
            .create();
    private final int TIMEOUT = 10;


    @Before
    public void init() {
        latch = new CountDownLatch(1);
    }


    @Test
    public void testLogin() throws InterruptedException {
        Assert.assertNotNull(loginIdentity);
        ApiManager.getInstance().loginUser(loginIdentity, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isRequestSuccess = true;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
    }


    @Test
    public void testGetUser() throws InterruptedException {
        ApiManager.getInstance().getUser(new ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                currentUser = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentUser);
        Assert.assertNotNull(currentUser.getUsername());
    }


    @Test
    public void testUpdateUser() {

    }

    @Test
    public void testDeleteUser() {

    }

    @Test
    public void testCreateCreditCard() {

    }

    @Test
    public void testGetCards() throws InterruptedException {
        Assert.assertNotNull(currentUser);
        currentUser.getCreditCards(2, 0, new ApiCallback<Collections.CreditCardCollection>() {
            @Override
            public void onSuccess(Collections.CreditCardCollection result) {
                isRequestSuccess = true;
                cardsCollection = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(cardsCollection);
        Assert.assertFalse(cardsCollection.getTotalResults() == 0);
    }


    @Test
    public void testCreateDevice() {

    }

    @Test
    public void testGetDevices() {

    }


    @After
    public void after() throws Exception {
        latch = null;
        isRequestSuccess = false;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        latch = null;
        currentUser = null;
        cardsCollection = null;
    }

}
