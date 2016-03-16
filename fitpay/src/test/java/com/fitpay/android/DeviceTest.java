package com.fitpay.android;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeviceTest {

    private static Steps steps = null;

    @BeforeClass
    public static void init(){
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
    public void test03_getDevices() throws InterruptedException {
        steps.getDevices();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        steps.destroy();
    }
}

