package com.fitpay.android;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.DeviceTypes;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.LoginIdentity;
import com.fitpay.android.api.models.card.Address;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.utils.ApiManager;
import com.fitpay.android.utils.TimestampUtils;
import com.fitpay.android.utils.ValidationException;

import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TestActions {

    protected final int TIMEOUT = 10;

    protected Random random = new Random();

    protected String userName = null;
    protected String pin = null;
    protected LoginIdentity loginIdentity = null;

    @BeforeClass
    public static void init() {
        ApiManager.init(TestConstants.BASE_URL);
    }

    protected void doLogin(LoginIdentity loginIdentity) throws Exception{

        final CountDownLatch latch = new CountDownLatch(1);
        ApiManager.getInstance().loginUser(loginIdentity, getSuccessDeterminingCallback(latch));
        boolean completed = latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertTrue("login did not complete successfully", completed);
    }



    protected User getUser() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        UserProvidingCallback callback = new UserProvidingCallback(latch);

        ApiManager.getInstance().getUser(callback);

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        User user = callback.getUser();
        Assert.assertNotNull(user);
        return user;
    }

    protected LoginIdentity getTestLoginIdentity(String userName, String pin) throws ValidationException {

        LoginIdentity loginIdentity = new LoginIdentity.Builder()
                    .setUsername(userName)
                    .setPassword(pin)
                    .setClientId(TestConstants.CLIENT_ID)
                    .setRedirectUri(TestConstants.BASE_URL)
                    .create();
        return loginIdentity;

    }

    protected ApiCallback<Void> getSuccessDeterminingCallback(final CountDownLatch latch) {

        ApiCallback<Void> callback = new ApiCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
               if (null != latch) {
                    latch.countDown();
               }
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                fail();
            }
        };
        return callback;
    }

    protected String getRandomLengthString(int minLength, int maxLength) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        int length = minLength;
        if (maxLength > minLength) {
            length = minLength + random.nextInt(maxLength - minLength);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(chars.length());
            sb.append(chars.substring(pos, pos+ 1));
        }
        return sb.toString();
    }

    protected String getRandomLengthNumber(int minLength, int maxLength) {
        String chars = "0123456789";
        int length = minLength;
        if (maxLength > minLength) {
            length = minLength + random.nextInt(maxLength - minLength);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(chars.length());
            sb.append(chars.substring(pos, pos+ 1));
        }
        return sb.toString();
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
                .create();
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
                .setDeviceType(DeviceTypes.SMART_STRAP)
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
                .create();

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
                .create();

        return newDevice;

    }

    protected Device createDevice(User user, Device device) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DeviceProvidingCallback callback = new DeviceProvidingCallback(latch);
        user.createDevice(device, callback);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Device createdDevice = callback.getDevice();
        return createdDevice;
    }


    public class UserProvidingCallback implements ApiCallback<User> {

        private User user;
        private int errorCode = -1;
        private String errorMessage;
        private CountDownLatch latch;

        public UserProvidingCallback() {}

        public UserProvidingCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(User result) {
            this.user = result;
            if (null != latch) {
                latch.countDown();
            }
        }

        @Override
        public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            if (null != latch) {
                latch.countDown();
            }
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public User getUser() {
            return user;
        }
    }

    public class CreditCardProvidingCallback implements ApiCallback<CreditCard> {

        private CreditCard creditCard;
        private int errorCode = -1;
        private String errorMessage;
        private CountDownLatch latch;

        public CreditCardProvidingCallback() {}

        public CreditCardProvidingCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(CreditCard result) {
            this.creditCard = result;
            if (null != latch) {
                latch.countDown();
            }
        }

        @Override
        public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            if (null != latch) {
                latch.countDown();
            }
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public CreditCard getCreditCard() {
            return creditCard;
        }
    }


    public class CreditCardCollectionProvidingCallback implements ApiCallback<Collections.CreditCardCollection> {

        private Collections.CreditCardCollection creditCards;
        private int errorCode = -1;
        private String errorMessage;
        private CountDownLatch latch;

        public CreditCardCollectionProvidingCallback() {}

        public CreditCardCollectionProvidingCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(Collections.CreditCardCollection result) {
            this.creditCards = result;
            if (null != latch) {
                latch.countDown();
            }
        }

        @Override
        public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            if (null != latch) {
                latch.countDown();
            }
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Collections.CreditCardCollection getCreditCards() {
            return creditCards;
        }
    }

    public class DeviceProvidingCallback implements ApiCallback<Device> {

        private Device device;
        private int errorCode = -1;
        private String errorMessage;
        private CountDownLatch latch;

        public DeviceProvidingCallback() {}

        public DeviceProvidingCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(Device result) {
            this.device = result;
            if (null != latch) {
                latch.countDown();
            }
        }

        @Override
        public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            if (null != latch) {
                latch.countDown();
            }
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Device getDevice() {
            return device;
        }
    }



    public class DeviceCollectionProvidingCallback implements ApiCallback<Collections.DeviceCollection> {

        private Collections.DeviceCollection devices;
        private int errorCode = -1;
        private String errorMessage;
        private CountDownLatch latch;

        public DeviceCollectionProvidingCallback() {}

        public DeviceCollectionProvidingCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(Collections.DeviceCollection result) {
            this.devices = result;
            if (null != latch) {
                latch.countDown();
            }
        }

        @Override
        public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            if (null != latch) {
                latch.countDown();
            }
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Collections.DeviceCollection getDevices() {
            return devices;
        }
    }

}