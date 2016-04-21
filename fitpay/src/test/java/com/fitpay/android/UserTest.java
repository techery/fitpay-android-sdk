package com.fitpay.android;

import com.fitpay.android.utils.ApiManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {

    private static Steps steps = null;

    @BeforeClass
    public static void init() {
        ApiManager.init(TestConstants.BASE_URL);
        steps = new Steps();
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
    public void test03_selfUser() throws InterruptedException {
        steps.selfUser();
    }

    @Test
    @Ignore
    public void test04_updateUser() throws InterruptedException {
        steps.updateUser();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        steps.destroy();
        steps = null;
    }

}