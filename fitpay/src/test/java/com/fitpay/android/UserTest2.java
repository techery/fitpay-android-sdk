package com.fitpay.android;

import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.models.user.UserCreateRequest;
import com.fitpay.android.api.callbacks.ResultProvidingCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class UserTest2 extends TestActions {

    private User user = null;

    @Before
    public void setup() throws Exception {
        userName = TestUtils.getRandomLengthString(5, 10)
                + "@" + TestUtils.getRandomLengthString(5, 10) + "." + TestUtils.getRandomLengthString(4, 10);
        pin = TestUtils.getRandomLengthNumber(4, 4);

        UserCreateRequest user = getNewTestUser(userName, pin);
        User createdUser = createUser(user);
        assertNotNull("user should have been created", createdUser);

        loginIdentity = getTestLoginIdentity(userName, pin);
        doLogin(loginIdentity);
    }

    @After
    public void deleteUser() throws Exception {
        if (null != this.user) {
            final CountDownLatch latch = new CountDownLatch(1);
            ResultProvidingCallback<Void> callback = new ResultProvidingCallback<>(latch);
            this.user.deleteUser(callback);
            latch.await(TIMEOUT, TimeUnit.SECONDS);
        }
    }


    @Test
    public void testCanGetUser() throws Exception {
        this.user = getUser();
        assertNotNull(user);
        assertEquals("userName", userName, user.getUsername());
        assertEquals("email", userName, user.getEmail());
        assertNotNull("user id", user.getId());
        assertNotNull("created ts", user.getCreatedTsEpoch());
    }

    @Test
    public void testCanRepeatLogin() throws Exception {
        this.user = getUser();
        assertNotNull(user);
        doLogin(loginIdentity);
        User user2  = getUser();
        assertEquals("should be the same user", user.getId(), user2.getId());
    }

    @Test
    public void testUserCanGetSelf() throws Exception {
        this.user = getUser();

        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<User> callback = new ResultProvidingCallback<>(latch);
        user.self(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        User user2 = callback.getResult();

        assertEquals("user self had error code (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        assertEquals("user id", user.getId(), user2.getId());
        assertEquals("email", user.getEmail(), user2.getEmail());
        assertEquals("user name", user.getUsername(), user2.getUsername());
        assertEquals("build ts", user.getCreatedTsEpoch(), user2.getCreatedTsEpoch());
    }

    @Test
    public void testNewUserCanGetCards() throws Exception {
        this.user = getUser();

        Collections.CreditCardCollection creditCards = getCreditCards(user);

        assertNotNull("credit card collection", creditCards);
        assertEquals("number of cards a new user has", 0, creditCards.getTotalResults());

    }

    @Test
    public void testNewUserCanGetDevices() throws Exception {
        this.user = getUser();

        Collections.DeviceCollection collection = getDevices(user);

        assertNotNull("device collection", collection);
        assertEquals("number of devices a new user has", 0, collection.getTotalResults());

    }


    @Test
    @Ignore
    //TODO Comparing to edge tests, should not be able to get user after delete.   Why can we here?
    public void testCantGetDeletedUser() throws Exception {
        this.user = getUser();
        assertNotNull(user);

        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Void> callback = new ResultProvidingCallback<>(latch);
        this.user.deleteUser(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("delete error code", -1, callback.getErrorCode());

        User deletedUser = getUser();
        assertNull("user should not exist", deletedUser);
        this.user = null;
    }


}