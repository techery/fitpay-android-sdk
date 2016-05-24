package com.fitpay.android.webview.impl;

import com.fitpay.android.TestActions;
import com.fitpay.android.TestUtils;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.models.user.UserCreateRequest;
import com.fitpay.android.webview.WebViewCommunicator;
import com.fitpay.android.webview.callback.OnTaskCompleted;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

//import com.fitpay.android.api.models.user.UserCreateRequest;

/**
 * Created by tgs on 5/20/16.
 */
public class WebViewCommunictorTest extends TestActions {

    private User user;

    @Before
    public void setup() throws Exception {
        userName = TestUtils.getRandomLengthString(5, 10) + "@"
                + TestUtils.getRandomLengthString(5, 10) + "." + TestUtils.getRandomLengthString(4, 10);
        pin = TestUtils.getRandomLengthNumber(4, 4);

        UserCreateRequest user = getNewTestUser(userName, pin);
        User createdUser = createUser(user);
        assertNotNull("user should have been created", createdUser);

        loginIdentity = getTestLoginIdentity(userName, pin);
        doLogin(loginIdentity);

        this.user = getUser();
        assertNotNull(user);
        Device device = getTestDevice();

        Device createdDevice = createDevice(this.user, device);

        assertNotNull("device", createdDevice);

    }


    @Test
    @Ignore  // can only be run manually since needs valid user token
    public void testSendUserData() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        OnTaskCompleted callback = new BlockingOnTaskCompletedCallback(latch);

        String deviceId = "458ba377-5b73-49f6-b284-1846793bb38c";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhOTZlN2M0Yi0zMThkLTM4ZWItOTk3ZS05ZTgwZWMwNzUzZmEiLCJ1c2VyX25hbWUiOiJsZWpmZmdpb0B2amx1cGZ0b2wudGJha292enBoIiwiaXNzIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwODBcL3VhYVwvb2F1dGhcL3Rva2VuIiwiY2xpZW50X2lkIjoicGFnYXJlIiwiYXVkIjpbInBhZ2FyZSIsInVzZXIiLCJ0cmFuc2FjdGlvbnMiLCJkZXZpY2VzIiwiY3JlZGl0Q2FyZHMiXSwiemlkIjoidWFhIiwidXNlcl9pZCI6ImE5NmU3YzRiLTMxOGQtMzhlYi05OTdlLTllODBlYzA3NTNmYSIsImF6cCI6InBhZ2FyZSIsInNjb3BlIjpbInVzZXIucmVhZCIsInVzZXIud3JpdGUiLCJ0cmFuc2FjdGlvbnMucmVhZCIsImRldmljZXMud3JpdGUiLCJkZXZpY2VzLnJlYWQiLCJjcmVkaXRDYXJkcy53cml0ZSIsImNyZWRpdENhcmRzLnJlYWQiXSwiZXhwIjoxNDYzNzczMjAyLCJpYXQiOjE0NjM3NzE0MDIsImp0aSI6IjFlYTljYzRiLWJkYzctNGRiOS1hOGMzLTUwZmI2NmMyZTllZCIsImVtYWlsIjoibGVqZmZnaW9AdmpsdXBmdG9sLnRiYWtvdnpwaCIsImNpZCI6InBhZ2FyZSJ9.5yphjJ1qFqAh4Af62xB5SwjdQLCRK1g5JvlJxmG2JxQ";
        String userId = "a96e7c4b-318d-38eb-997e-9e80ec0753fa";
        WebViewCommunicator wvc = new WebViewCommunicatorStubImpl(null, -1, callback);
        String ack = wvc.sendUserData(null, deviceId, token, userId);
        assertEquals("ack value", "{\"status\":\"OK\"}", ack);

        boolean completed = latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertTrue("callback completed", completed);

    }

    public class BlockingOnTaskCompletedCallback implements OnTaskCompleted {

        private CountDownLatch latch;

        public BlockingOnTaskCompletedCallback() {}

        public BlockingOnTaskCompletedCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onTaskCompleted(String result) {
            if (null != latch) {
                latch.countDown();
            }
        }
    }

}
