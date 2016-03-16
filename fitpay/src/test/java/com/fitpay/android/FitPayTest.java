package com.fitpay.android;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.Commit;
import com.fitpay.android.api.models.LoginIdentity;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.utils.ApiManager;
import com.fitpay.android.utils.TimestampUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.JVM)
public class FitPayTest {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static User currentUser;
    private static Device currentDevice;
    private static Commit currentCommit;
    private static boolean isRequestSuccess = false;

    private final LoginIdentity loginIdentity = new LoginIdentity.Builder()
//                .setUsername("skynet17@ya.ru")
            .setUsername("test1@test.test")
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


    @Ignore //{"message":"error applying patch: no such path in target JSON document"}
    @Test
    public void testUpdateUser() throws InterruptedException {
        Assert.assertNotNull(currentUser);

        String firstName = "John";
        String lastName = "Doe";
        long currentTimestamp = System.currentTimeMillis();
        String timestampString = TimestampUtils.getISO8601StringForTime(currentTimestamp);
        String termsVersion = "0.0.2";
        User patchingUser = new User.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setBirthDate(currentTimestamp)
                .setOriginAccountCreatedAt(currentTimestamp)
                .setTermsAcceptedAt(currentTimestamp)
                .setTermsVersion(termsVersion)
                .create();

        currentUser.updateUser(patchingUser, new ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                isRequestSuccess = true;
                currentUser = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(currentUser);
        Assert.assertEquals(currentUser.getFirstName(), firstName);
        Assert.assertEquals(currentUser.getLastName(), lastName);
        Assert.assertEquals(currentUser.getBirthDate(), timestampString);
        Assert.assertEquals(currentUser.getOriginAccountCreatedTs(), timestampString);
        Assert.assertEquals(currentUser.getTermsAcceptedTs(), timestampString);
        Assert.assertEquals(currentUser.getTermsVersion(), termsVersion);
    }

    @Test
    public void testDeleteUser() throws InterruptedException {
        Assert.assertNotNull(currentUser);
        currentUser.deleteUser(new ApiCallback<Void>() {
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
    public void testCreateCreditCard() {

    }

    @Test
    public void testGetCards() throws InterruptedException {
        Assert.assertNotNull(currentUser);
        currentUser.getCreditCards(2, 0, new ApiCallback<Collections.CreditCardCollection>() {
            @Override
            public void onSuccess(Collections.CreditCardCollection result) {
                isRequestSuccess = true;
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getTotalResults() == 0);
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
    public void testCreateDevice() throws InterruptedException {
        Assert.assertNotNull(currentUser);

        String manufacturerName = "X111";
        String deviceName = "X-111";
        String firmwareRevision = "111.111";
        String hardwareRevision = "1.1.1";
        String modelNumber = "AB111";
        String serialNumber = "1111AB";
        String softwareRevision = "1.1.1";
        String systemId = "0x111AA";
        String oSName = "A1111";
        String licenseKey = "aaaaaa-1111-1111-1111-111111111111";
        String bdAddress = "bbbbbb-1111-1111-1111-111111111111";
        long pairingTs = System.currentTimeMillis();
        String stringTimestamp = TimestampUtils.getISO8601StringForTime(pairingTs);
        String secureElementId = "cccccc-1111-1111-1111-1111111111";
        Device newDevice = new Device.Builder()
                .setDeviceType(DeviceTypes.WATCH)
                .setManufacturerName(manufacturerName)
                .setDeviceName(deviceName)
                .setFirmwareRevision(firmwareRevision)
                .setHardwareRevision(hardwareRevision)
                .setModelNumber(modelNumber)
                .setSerialNumber(serialNumber)
                .setSoftwareRevision(softwareRevision)
                .setSystemId(systemId)
                .setOSName(oSName)
                .setLicenseKey(licenseKey)
                .setBdAddress(bdAddress)
                .setPairingTs(pairingTs)
                .setSecureElementId(secureElementId)
                .create();

        currentUser.createDevice(newDevice, new ApiCallback<Device>() {
            @Override
            public void onSuccess(Device result) {
                isRequestSuccess = true;
                currentDevice = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(currentDevice);
        Assert.assertEquals(currentDevice.getManufacturerName(), manufacturerName);
        Assert.assertEquals(currentDevice.getDeviceName(), deviceName);
        Assert.assertEquals(currentDevice.getFirmwareRevision(), firmwareRevision);
        Assert.assertEquals(currentDevice.getHardwareRevision(), hardwareRevision);
        Assert.assertEquals(currentDevice.getModelNumber(), modelNumber);
        Assert.assertEquals(currentDevice.getSerialNumber(), serialNumber);
        Assert.assertEquals(currentDevice.getSoftwareRevision(), softwareRevision);
        Assert.assertEquals(currentDevice.getSystemId(), systemId);
        Assert.assertEquals(currentDevice.getOsName(), oSName);
//        Assert.assertEquals(currentDevice.getLicenseKey(), licenseKey);//todo check
//        Assert.assertEquals(currentDevice.getBdAddress(), bdAddress);
        Assert.assertEquals(currentDevice.getPairingTs(), stringTimestamp);
        Assert.assertEquals(currentDevice.getSecureElementId(), secureElementId);
    }

    @Test
    public void testGetDevices() throws InterruptedException {
        Assert.assertNotNull(currentUser);
        currentUser.getDevices(2, 0, new ApiCallback<Collections.DeviceCollection>() {
            @Override
            public void onSuccess(Collections.DeviceCollection result) {
                isRequestSuccess = true;
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getResults());
                Assert.assertTrue(result.getResults().size() == 1);
                currentDevice = result.getResults().get(0);
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(currentDevice);
    }

    @Test
    public void testSelfDevice() throws InterruptedException {
        Assert.assertNotNull(currentDevice);
        currentDevice.self(new ApiCallback<Device>() {
            @Override
            public void onSuccess(Device result) {
                isRequestSuccess = true;
                Assert.assertNotNull(result);
                currentDevice = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(currentDevice);
    }

    @Test
    public void testUpdateDevice() throws InterruptedException {
        Assert.assertNotNull(currentDevice);
        String firmwareRevision = "222.222";
        String softwareRevision = "2.2.2";
        Device newDevice = new Device.Builder()
                .setFirmwareRevision(firmwareRevision)
                .setSoftwareRevision(softwareRevision)
                .create();

        currentDevice.updateDevice(newDevice, new ApiCallback<Device>() {
            @Override
            public void onSuccess(Device result) {
                isRequestSuccess = true;
                currentDevice = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(currentDevice);
        Assert.assertEquals(currentDevice.getFirmwareRevision(), firmwareRevision);
        Assert.assertEquals(currentDevice.getSoftwareRevision(), softwareRevision);
    }

    @Test
    public void testGetCommits() throws InterruptedException {
        Assert.assertNotNull(currentDevice);
        currentDevice.getCommits(2, 0, new ApiCallback<Collections.CommitsCollection>() {
            @Override
            public void onSuccess(Collections.CommitsCollection result) {
                isRequestSuccess = true;
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getResults());
                Assert.assertTrue(result.getResults().size() > 0);
                currentCommit = result.getResults().get(0);
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess);
        Assert.assertNotNull(currentCommit);
        Assert.assertNotNull(currentCommit.getCommitId());
        Assert.assertNotNull(currentCommit.getCommitType());
        Assert.assertNotNull(currentCommit.getCreatedTs());
        Assert.assertNotNull(currentCommit.getPayload());
    }

    @Test
    public void testDeleteDevice() throws InterruptedException {
        Assert.assertNotNull(currentDevice);
        currentDevice.deleteDevice(new ApiCallback<Void>() {
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


    @After
    public void after() throws Exception {
        latch = null;
        isRequestSuccess = false;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        latch = null;
        currentUser = null;
        currentDevice = null;
    }

}
