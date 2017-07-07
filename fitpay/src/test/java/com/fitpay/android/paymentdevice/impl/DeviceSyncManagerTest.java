package com.fitpay.android.paymentdevice.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.fitpay.android.TestActions;
import com.fitpay.android.TestUtils;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.LoginIdentity;
import com.fitpay.android.api.models.user.UserCreateRequest;
import com.fitpay.android.paymentdevice.DeviceSyncManager;
import com.fitpay.android.paymentdevice.callbacks.PaymentDeviceListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.events.CommitSuccess;
import com.fitpay.android.paymentdevice.events.PaymentDeviceOperationFailed;
import com.fitpay.android.paymentdevice.impl.mock.MockPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by ssteveli on 7/6/17.
 */


public class DeviceSyncManagerTest extends TestActions {

    private Context mContext;
    private DeviceSyncManager syncManager;
    protected IPaymentDeviceConnector mockPaymentDevice;

    private Device device;

    private SyncCompleteListener listener;
    private CountDownLatch executionLatch;
    private CountDownLatch connectionLatch;

    @Before
    @Override
    public void testActionsSetup() throws Exception {
        SharedPreferences sp = Mockito.mock(SharedPreferences.class);
        Mockito.when(sp.getAll()).thenReturn(Collections.emptyMap());

        SharedPreferences.Editor spEditor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sp.edit()).thenReturn(spEditor);
        Mockito.when(spEditor.commit()).thenReturn(true);

        mContext = Mockito.mock(Context.class);
        Mockito.when(mContext.getSharedPreferences(Matchers.anyString(), Matchers.eq(Context.MODE_PRIVATE))).thenReturn(sp);

        syncManager = new DeviceSyncManager(mContext, Constants.getExecutor());
        syncManager.onCreate();

        mockPaymentDevice = new MockPaymentDeviceConnector();

        userName = TestUtils.getRandomLengthString(5, 10) + "@"
                + TestUtils.getRandomLengthString(5, 10) + "." + TestUtils.getRandomLengthString(4, 10);
        pin = TestUtils.getRandomLengthNumber(4, 4);

        UserCreateRequest userCreateRequest = getNewTestUser(userName, pin);
        createUser(userCreateRequest);

        assertTrue(doLogin(new LoginIdentity.Builder()
                .setPassword(pin)
                .setUsername(userName)
                .build()));
        this.user = getUser();

        this.device = createDevice(this.user, getTestDevice());
        assertNotNull(this.device);

        Properties props = new Properties();
        props.put(MockPaymentDeviceConnector.CONFIG_CONNECTED_RESPONSE_TIME, "0");
        mockPaymentDevice.init(props);

        assertEquals("payment service is not initialized", States.INITIALIZED, mockPaymentDevice.getState());

        connectionLatch = new CountDownLatch(1);

        NotificationManager.getInstance().addListenerToCurrentThread(new PaymentDeviceListener() {
             @Override
             public void onDeviceInfoReceived(Device device) {

             }

             @Override
             public void onDeviceOperationFailed(PaymentDeviceOperationFailed failure) {

             }

             @Override
             public void onNFCStateReceived(boolean isEnabled, byte errorCode) {

             }

             @Override
             public void onNotificationReceived(byte[] data) {

             }

             @Override
             public void onApplicationControlReceived(byte[] data) {

             }

             @Override
             public void onDeviceStateChanged(@Connection.State int state) {
                 if (States.CONNECTED == state) {
                     connectionLatch.countDown();
                 }
             }
         });

        mockPaymentDevice.connect();

        try {
            connectionLatch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertEquals("payment service should be connected", States.CONNECTED, mockPaymentDevice.getState());

        this.executionLatch = new CountDownLatch(4);
        this.listener = new SyncCompleteListener(executionLatch);
        NotificationManager.getInstance().addListener(this.listener, Schedulers.from(Constants.getExecutor()));
    }

    @After
    public void cleanup() {
        if (syncManager != null) {
            syncManager.onDestroy();
        }

        NotificationManager.getInstance().removeListener(this.listener);

        mContext = null;
    }

    @Test
    public void happyPathSyncTest() throws Exception {
        SyncRequest request = SyncRequest.builder()
                .setConnector(mockPaymentDevice)
                .setUser(user)
                .setDevice(device)
                .build();

        syncManager.add(request);

//        executionLatch.await();
//
//        assertTrue(listener.getSyncEvents().size() > 0);
//        assertEquals(3, listener.getCommits().size());

        Thread.sleep(30000);
    }

    private class SyncCompleteListener extends Listener {
        private final List<Sync> syncEvents = new ArrayList<>();
        private final List<CommitSuccess> commits = new ArrayList<>();
        private final CountDownLatch executionLatch;

        private SyncCompleteListener(CountDownLatch latch) {
            this.executionLatch = latch;
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
            mCommands.put(CommitSuccess.class, data -> onCommitSuccess((CommitSuccess) data));
        }

        public void onSyncStateChanged(Sync syncEvent) {
            System.out.println("SYNC EVENT: " + syncEvent);
            syncEvents.add(syncEvent);

            if (executionLatch != null && syncEvent.getState() == States.COMPLETED) {
                executionLatch.countDown();
            }
        }

        public void onCommitSuccess(CommitSuccess commit) {
            System.out.println("COMMIT SUCCESS: " + commit);
            commits.add(commit);

            executionLatch.countDown();
        }

        public List<Sync> getSyncEvents() {
            return syncEvents;
        }

        public List<CommitSuccess> getCommits() {
            return commits;
        }
    }

}
