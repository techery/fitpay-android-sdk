package com.fitpay.android;

import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.models.user.LoginIdentity;
import com.fitpay.android.api.models.Transaction;
import com.fitpay.android.api.models.card.Address;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.Reason;
import com.fitpay.android.api.models.card.VerificationMethod;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.models.user.UserCreateRequest;
import com.fitpay.android.callback.ResultProvidingCallback;
import com.fitpay.android.api.ApiManager;
import com.fitpay.android.utils.TimestampUtils;
import com.fitpay.android.utils.ValidationException;

import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TestActions {

    protected final int TIMEOUT = 10;

    protected Random random = new Random();

    protected String userName = null;
    protected String pin = null;
    protected LoginIdentity loginIdentity = null;

    @BeforeClass
    public static void init() {
        ApiManager.init(TestConstants.getConfig());
    }

    protected User createUser(UserCreateRequest user) throws Exception{
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<User> callback = new ResultProvidingCallback<>(latch);
        ApiManager.getInstance().createUser(user, callback);
        boolean completed = latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected boolean doLogin(LoginIdentity loginIdentity) throws Exception{
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Void> callback = new ResultProvidingCallback<>(latch);
        ApiManager.getInstance().loginUser(loginIdentity, callback);
        boolean completed = latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertTrue("login did not complete successfully", completed);
        assertEquals("login error code. (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        return completed;
    }



    protected User getUser() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        ResultProvidingCallback<User> callback = new ResultProvidingCallback<>(latch);

        ApiManager.getInstance().getUser(callback);

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        User user = callback.getResult();
        Assert.assertNotNull(user);
        return user;
    }

    protected LoginIdentity getTestLoginIdentity(String userName, String pin) throws ValidationException {

        LoginIdentity loginIdentity = new LoginIdentity.Builder()
                    .setUsername(userName)
                    .setPassword(pin)
                    .build();
        return loginIdentity;

    }

    protected UserCreateRequest getNewTestUser(String userName, String pin) throws ValidationException {

        UserCreateRequest user = new UserCreateRequest.Builder()
                .email(userName)
                .pin(pin)
                .build();
        return user;

    }

    protected CreditCard getTestCreditCard(String pan) {
        String cardName = "TEST CARD";
        int expYear = 2018;
        int expMonth = 10;
        String city = "Boulder";
        String state = "CO";
        String postalCode = "80302";
        String countryCode = "US";
        String street1 = "1035 Pearl St";
        String cvv = "133";

        Address address = new Address();
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountryCode(countryCode);
        address.setStreet1(street1);

        CreditCard creditCard = new CreditCard.Builder()
                .setCVV(cvv)
                .setPAN(pan)
                .setExpDate(expYear, expMonth)
                .setAddress(address)
                .setName(cardName)
                .build();
        return creditCard;
    }

    public Device getTestDevice()  {

        String manufacturerName = "X111";
        String deviceName = "TEST_DEVICE";
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
                .setDeviceType(DeviceTypes.ACTIVITY_TRACKER)
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
                .build();

        return newDevice;

    }

    public Device getPoorlyDefinedDevice()  {

        String deviceName = "TEST_DEVICE";
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
                .setDeviceName(deviceName)
                .build();

        return newDevice;

    }

    public Device getPoorlyDeviceTestSmartStrapDevice()  {

        String manufacturerName = "X111";
        String deviceName = "TEST_DEVICE";
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
                .setDeviceType(DeviceTypes.SMART_STRAP)
                .setDeviceName(deviceName)
                .build();

        return newDevice;

    }

    protected Device createDevice(User user, Device device) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Device> callback = new ResultProvidingCallback<>(latch);
        user.createDevice(device, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected CreditCard createCreditCard(User user, CreditCard creditCard)  throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<CreditCard> callback = new ResultProvidingCallback<>(latch);
        user.createCreditCard(creditCard, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected CreditCard getCreditCard(CreditCard creditCard)  throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<CreditCard> callback = new ResultProvidingCallback<>(latch);
        creditCard.self(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("get credit card had error code.  (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        return callback.getResult();
    }


    protected Collections.CreditCardCollection getCreditCards(User user) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Collections.CreditCardCollection> callback = new ResultProvidingCallback<>(latch);
        user.getCreditCards(10, 0 , callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("get credit cards had error code.  (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        return callback.getResult();
    }

    protected CreditCard acceptTerms(CreditCard creditCard) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<CreditCard> callback = new ResultProvidingCallback<>(latch);
        creditCard.acceptTerms(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected CreditCard declineTerms(CreditCard creditCard) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<CreditCard> callback = new ResultProvidingCallback<>(latch);
        creditCard.declineTerms(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected CreditCard deactivateCard(CreditCard creditCard, Reason reason) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<CreditCard> callback = new ResultProvidingCallback<>(latch);
        creditCard.deactivate(reason, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected CreditCard reactivateCard(CreditCard creditCard, Reason reason) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<CreditCard> callback = new ResultProvidingCallback<>(latch);
        creditCard.reactivate(reason, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }


    protected void makeDefaultCard(CreditCard creditCard) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Void> callback = new ResultProvidingCallback<>(latch);
        creditCard.makeDefault(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("make default error code", -1, callback.getErrorCode());
    }


    protected void deleteCard(CreditCard creditCard) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Void> callback = new ResultProvidingCallback<>(latch);
        creditCard.deleteCard(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("delete error code", -1, callback.getErrorCode());
    }

    protected VerificationMethod selectVerificationMethod(VerificationMethod method) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<VerificationMethod> callback = new ResultProvidingCallback<>(latch);
        method.select(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected VerificationMethod verifyVerificationMethod(VerificationMethod method, String verificationCode) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<VerificationMethod> callback = new ResultProvidingCallback<>(latch);
        method.verify(verificationCode, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        return callback.getResult();
    }

    protected Collections.TransactionCollection getCardTransactions(CreditCard card) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Collections.TransactionCollection> callback = new ResultProvidingCallback<>(latch);
        card.getTransactions(10, 0 , callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("get device transactions error code.  (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        return callback.getResult();
    }

    protected Transaction getTransaction(Transaction transaction)  throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Transaction> callback = new ResultProvidingCallback<>(latch);
        transaction.self(callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("get device transaction error code.  (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        return callback.getResult();
    }


    protected Collections.DeviceCollection getDevices(User user) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ResultProvidingCallback<Collections.DeviceCollection> callback = new ResultProvidingCallback<>(latch);
        user.getDevices(10, 0 , callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals("get devices error code.  (message: " + callback.getErrorMessage() + ")", -1, callback.getErrorCode());
        return callback.getResult();
    }



}