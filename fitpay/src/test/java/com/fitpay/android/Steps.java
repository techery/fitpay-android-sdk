package com.fitpay.android;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.CardInitiators;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.LoginIdentity;
import com.fitpay.android.api.models.card.Address;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.Reason;
import com.fitpay.android.api.models.card.VerificationMethod;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.utils.ApiManager;
import com.fitpay.android.utils.ValidationException;

import org.junit.Assert;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vlad on 16.03.2016.
 */
public class Steps {
    private final int TIMEOUT = 10;

    private User currentUser;
    private Collections.CreditCardCollection cardsCollection;
    private Collections.DeviceCollection devicesCollection;
    private CreditCard currentCard;
    private Device currentDevice;

    public void destroy(){
        currentUser = null;
        cardsCollection = null;
        devicesCollection = null;
    }

    public void login() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] isRequestSuccess = {false};

        LoginIdentity loginIdentity = null;

        try {
            loginIdentity = new LoginIdentity.Builder()
                    .setUsername("test@test.test")
                    .setPassword("1221")
                    .setClientId("pagare")
                    .setRedirectUri("https://demo.pagare.me")
                    .create();
        } catch (ValidationException e) {
        }

        Assert.assertNotNull(loginIdentity);

        ApiManager.getInstance().loginUser(loginIdentity, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isRequestSuccess[0] = true;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess[0]);
    }

    public void getUser() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

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

    public void createCard() throws InterruptedException {
        Assert.assertNotNull(currentUser);

        final CountDownLatch latch = new CountDownLatch(1);

        Address address = new Address();
        address.setCity("Boulder");
        address.setState("CO");
        address.setPostalCode("80302");
        address.setCountry("US");
        address.setStreet1("1035 Pearl St");

        CreditCard creditCard = new CreditCard.Builder()
                .setCVV("133")
                .setPAN("5454545454545454")
                .setExpDate(2018, 10)
                .setAddress(address)
                .setName("TEST CARD")
                .create();

        currentUser.createCreditCard(creditCard, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
        Assert.assertEquals(currentCard.getName(), "TEST CARD");
        Assert.assertEquals(currentCard.getState(), "ELIGIBLE");
    }

    public void acceptTerms() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);

        currentCard.acceptTerms(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
        Assert.assertEquals(currentCard.getState(), "PENDING_VERIFICATION");
    }

    public void declineTerms() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);

        currentCard.declineTerms(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
        Assert.assertEquals(currentCard.getState(), "DECLINED_TERMS_AND_CONDITIONS");
    }

    public void selectCard() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);
        final VerificationMethod[] verificationMethods = {null};

        currentCard.getVerificationMethods().get(0).select(new ApiCallback<VerificationMethod>() {
            @Override
            public void onSuccess(VerificationMethod result) {
                verificationMethods[0] = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
        Assert.assertEquals(verificationMethods[0].getState(), "AWAITING_VERIFICATION");
    }

    public void verifyCard() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);
        final VerificationMethod[] verificationMethods = {null};

        currentCard.getVerificationMethods().get(0).verify("12345", new ApiCallback<VerificationMethod>() {
            @Override
            public void onSuccess(VerificationMethod result) {
                verificationMethods[0] = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
        Assert.assertEquals(verificationMethods[0].getState(), "VERIFIED");
    }

    public void updateCard() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);

        Address address = new Address();
        address.setCity("New York");
        address.setState("NY");

        CreditCard creditCard = new CreditCard.Builder()
                .setAddress(address)
                .create();

        currentCard.updateCard(creditCard, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
        Assert.assertEquals(currentCard.getAddress().getState(), "NY");
    }

    public void deactivateCard() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);

        Reason reason = new Reason();
        reason.setReason("lost");
        reason.setCausedBy(CardInitiators.INITIATOR_CARDHOLDER);

        currentCard.deactivate(reason, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
    }

    public void reactivateCard() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);

        Reason reason = new Reason();
        reason.setReason("found");
        reason.setCausedBy(CardInitiators.INITIATOR_CARDHOLDER);

        currentCard.reactivate(reason, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
    }

    public void makeDefault() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] isRequestSuccess = {false};

        currentCard.makeDefault(new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isRequestSuccess[0] = true;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue(isRequestSuccess[0]);
    }

    public void getCard() throws InterruptedException {
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);

        currentCard.self(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(currentCard);
    }

    public void getCards() throws InterruptedException {
        Assert.assertNotNull(currentUser);

        final CountDownLatch latch = new CountDownLatch(1);

        currentUser.getCreditCards(10, 0, new ApiCallback<Collections.CreditCardCollection>() {
            @Override
            public void onSuccess(Collections.CreditCardCollection result) {
                cardsCollection = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(cardsCollection);
    }

    public void deleteTestCards() throws InterruptedException {
        getCards();

        List<CreditCard> cards = cardsCollection.getResults();
        int size = cards.size();

        final CountDownLatch latch = new CountDownLatch(size);
        final int[] success = {0};

        for(int i = 0; i < size; i++){
            CreditCard card = cards.get(i);

            if(card.getName() == null || !card.getName().equals("TEST CARD")) {
                latch.countDown();
            } else {
                card.deleteCard(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        success[0]++;
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await(TIMEOUT * size, TimeUnit.SECONDS);

        getCards();
        Assert.assertEquals(cardsCollection.getResults().size(), 1);
    }

    public void getTransactions() throws InterruptedException {
        getCards();

        currentCard = cardsCollection.getResults().get(0);
        Assert.assertNotNull(currentCard);

        final CountDownLatch latch = new CountDownLatch(1);
        final Collections.TransactionCollection[] transactionCollection = {null};


        currentCard.getTransactions(10, 0, new ApiCallback<Collections.TransactionCollection>() {
            @Override
            public void onSuccess(Collections.TransactionCollection result) {
                transactionCollection[0] = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(cardsCollection);
        Assert.assertNotNull(transactionCollection[0]);
        Assert.assertNotNull(transactionCollection[0].getResults());
        Assert.assertFalse(transactionCollection[0].getResults().size() == 0);
    }


    public void getDevices() throws InterruptedException {
        Assert.assertNotNull(currentUser);

        final CountDownLatch latch = new CountDownLatch(1);

        currentUser.getDevices(10, 0, new ApiCallback<Collections.DeviceCollection>() {
            @Override
            public void onSuccess(Collections.DeviceCollection result) {
                devicesCollection = result;
                latch.countDown();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertNotNull(devicesCollection);
    }
}
