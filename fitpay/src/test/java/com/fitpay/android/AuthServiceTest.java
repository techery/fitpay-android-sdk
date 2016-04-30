package com.fitpay.android;

import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.models.user.UserCreateRequest;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by tgs on 4/27/16.
 */
public class AuthServiceTest extends TestActions {

    @Test
    public void canCreateUserAndGetToken() throws Exception {
        userName = TestUtils.getRandomLengthString(5, 10) + "@"
                + TestUtils.getRandomLengthString(5, 10) + "." + TestUtils.getRandomLengthString(4, 10);
        pin = TestUtils.getRandomLengthNumber(4, 4);

        UserCreateRequest user = getNewTestUser(userName, pin);
        User createdUser = createUser(user);
        assertNotNull("user should have been created", createdUser);

        loginIdentity = getTestLoginIdentity(userName, pin);
        boolean success = doLogin(loginIdentity);
        assertTrue("user auth not successful", success);

    }
}
