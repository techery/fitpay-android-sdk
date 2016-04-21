package com.fitpay.android;

import com.fitpay.android.utils.ApiManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreditCardTest {

    private static Steps steps = null;

    @BeforeClass
    public static void init() {
        steps = new Steps();
        ApiManager.init(TestConstants.BASE_URL);
    }

    @Test
    public void test01_loginUser() throws InterruptedException {
        steps.login();
    }

    @Test
    public void test02_getUser() throws InterruptedException {
        steps.getUser();
    }

    @Test
    public void test03_getTransactions() throws InterruptedException {
        steps.getTransactions();
    }

    @Test
    public void test04_createCard() throws InterruptedException {
        steps.createCard();
    }

    @Test
    public void test05_acceptCard() throws InterruptedException {
        steps.acceptTerms();
        steps.selfCard();
    }

    @Test
    public void test06_selectCard() throws InterruptedException {
        steps.selectCard();
        steps.selfCard();
    }

    @Test
    public void test07_verifyCard() throws InterruptedException {
        steps.verifyCard();
        steps.selfCard();
    }

    @Test
    @Ignore
    public void test08_updateCard() throws InterruptedException {
        steps.updateCard();
    }

    @Test
    public void test09_deactivateCard() throws InterruptedException {
        steps.deactivateCard();
    }

    @Test
    public void test10_reactivateCard() throws InterruptedException {
        steps.reactivateCard();
    }

    @Test
    public void test11_makeDefault() throws InterruptedException {
        steps.makeDefault();
    }

    @Test
    public void test12_declineTerms() throws InterruptedException {
        steps.createCard();
        steps.declineTerms();
    }

    @Test
    public void test12_deleteTestCards() throws InterruptedException {
        steps.deleteTestCards();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        steps.destroy();
        steps = null;
    }

}
