package com.fitpay.android;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.callbacks.ResultProvidingCallback;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by tgs on 4/21/16.
 */
public class DeviceTest2 extends TestActions {

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
    public void testCanAddDevice() throws Exception {
        Device device = getTestDevice();

       Device createdDevice = createDevice(user, device);

        assertNotNull("device", createdDevice);
        assertNotNull("device id", createdDevice.getDeviceIdentifier());
        assertEquals("device name", device.getDeviceName(), createdDevice.getDeviceName());
        assertEquals("device type", device.getDeviceType(), createdDevice.getDeviceType());
        assertEquals("firmware version", device.getFirmwareRevision(), createdDevice.getFirmwareRevision());
    }

    @Test
    public void testCantAddDeviceWithMissingType() throws Exception {
        Device device = getPoorlyDefinedDevice();

        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Device> callback = new ResultProvidingCallback<>(latch);
        user.createDevice(device, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Device createdDevice = callback.getResult();

        assertNull("created device", createdDevice);
        assertEquals("error code", 400, callback.getErrorCode());
    }

    @Test
    public void testCantAddDeviceWithMissingInfo() throws Exception {
        Device device = getPoorlyDeviceTestSmartStrapDevice();

        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Device> callback = new ResultProvidingCallback<>(latch);
        user.createDevice(device, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Device createdDevice = callback.getResult();

        assertNull("created device", createdDevice);
        assertEquals("error code", 400, callback.getErrorCode());
    }

    @Test
    public void testCanGetDevice() throws Exception {
        Device device = getTestDevice();

        Device createdDevice = createDevice(user, device);

        assertNotNull("device", createdDevice);

        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Device> callback = new ResultProvidingCallback<>(latch);
        createdDevice.self(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Device retrievedDevice = callback.getResult();

        assertNotNull("retrieved device", retrievedDevice);
        assertEquals("device id", createdDevice.getDeviceIdentifier(), retrievedDevice.getDeviceIdentifier());

    }

    @Test
    public void testCanDevicesWhenOnlyOneInCollection() throws Exception {
        Device device = getTestDevice();

        Device createdDevice = createDevice(user, device);
        assertNotNull("device", createdDevice);

        Collections.DeviceCollection devices = getDevices(user);

        assertNotNull("retrieved devices", devices);
        assertEquals("number of devices", 1, devices.getTotalResults());

        Device firstDevice = devices.getResults().get(0);
        assertNotNull("first device", firstDevice);
        assertEquals("device id", createdDevice.getDeviceIdentifier(), firstDevice.getDeviceIdentifier());

    }

    @Test
    public void testCanGetDeviceFromCollection() throws Exception {
        Device device = getTestDevice();

        Device createdDevice = createDevice(user, device);
        assertNotNull("device", createdDevice);

        Collections.DeviceCollection devices = getDevices(user);

        assertNotNull("retrieved devices", devices);
        assertEquals("number of devices", 1, devices.getTotalResults());

        Device firstDevice = devices.getResults().get(0);
        assertNotNull("first device", firstDevice);

        final CountDownLatch latch4 = new CountDownLatch(1);
        ResultProvidingCallback<Device> callback4 = new ResultProvidingCallback<>(latch4);
        createdDevice.self(callback4);
        latch4.await(TIMEOUT, TimeUnit.SECONDS);
        Device retrievedDevice = callback4.getResult();

        assertNotNull("retrieved device", retrievedDevice);
        assertEquals("device id", firstDevice.getDeviceIdentifier(), retrievedDevice.getDeviceIdentifier());
    }


    @Test
    public void testCanDevicesWhenTwoInCollection() throws Exception {
        Device device = getTestDevice();

        Device createdDevice = createDevice(user, device);
        assertNotNull("device", createdDevice);

        Device anotherCreatedDevice = createDevice(user, device);
        assertNotNull("device", anotherCreatedDevice);

        Collections.DeviceCollection devices = getDevices(user);

        assertNotNull("retrieved devices", devices);
        assertEquals("number of devices", 2, devices.getTotalResults());

        Device firstDevice = devices.getResults().get(0);
        assertNotNull("first device", firstDevice);
        assertEquals("device id", createdDevice.getDeviceIdentifier(), firstDevice.getDeviceIdentifier());

        Device secondDevice = devices.getResults().get(1);
        assertNotNull("second device", secondDevice);
        assertFalse("device ids in collection should not be equal", firstDevice.getDeviceIdentifier().equals(secondDevice.getDeviceIdentifier()));

    }

    @Test
    public void testCanDeleteDeviceFromCollection() throws Exception {
        Device device = getTestDevice();

        Device createdDevice = createDevice(user, device);
        assertNotNull("device", createdDevice);

        Device anotherCreatedDevice = createDevice(user, device);
        assertNotNull("device", anotherCreatedDevice);

        Collections.DeviceCollection devices = getDevices(user);

        assertNotNull("retrieved devices", devices);
        assertEquals("number of devices", 2, devices.getTotalResults());

        final CountDownLatch latch4 = new CountDownLatch(1);
        ApiCallback<Void> callback4 = getSuccessDeterminingCallback(latch4);
        createdDevice.deleteDevice(callback4);
        latch4.await(TIMEOUT, TimeUnit.SECONDS);

        devices = getDevices(user);

        assertNotNull("retrieved devices", devices);
        assertEquals("number of devices after delete of 1", 1, devices.getTotalResults());

        assertEquals("remaining device in collection", anotherCreatedDevice.getDeviceIdentifier(), devices.getResults().get(0).getDeviceIdentifier());
    }


}
