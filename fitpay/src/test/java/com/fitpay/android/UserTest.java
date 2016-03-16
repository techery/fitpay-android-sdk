package com.fitpay.android;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {

    private static Steps steps = null;

    @BeforeClass
    public static void init() {
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
    public void test04_updateUser() throws InterruptedException {
        steps.updateUser();
    }

    @Test
    public void test05_deleteUser() throws InterruptedException {
        steps.deleteUser();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        steps.destroy();
    }

}