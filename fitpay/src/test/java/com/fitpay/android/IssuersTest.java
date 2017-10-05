package com.fitpay.android;

import com.fitpay.android.api.ApiManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IssuersTest {

    private static Steps steps = null;

    @BeforeClass
    public static void init() {
        steps = new Steps();
        ApiManager.init(TestConstants.getConfig());
    }

    @Test
    public void test01_getIssuers() throws InterruptedException {
        steps.getIssuers();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        steps.destroy();
        steps = null;
    }
}