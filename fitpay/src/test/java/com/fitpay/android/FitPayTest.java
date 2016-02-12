package com.fitpay.android;

import android.content.Context;

import com.fitpay.android.units.APIUnit;
import com.fitpay.android.units.DeviceUnit;
import com.fitpay.android.units.RTMUnit;
import com.fitpay.android.units.Unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FitPayTest {

    @Mock
    Context context;

    @Test
    public void testInit() {
    }
}
