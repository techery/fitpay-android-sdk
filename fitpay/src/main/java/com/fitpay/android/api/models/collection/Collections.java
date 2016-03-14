package com.fitpay.android.api.models.collection;

import com.fitpay.android.api.models.Commit;
import com.fitpay.android.api.models.Transaction;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.device.Device;

/**
 * Created by Vlad on 14.03.2016.
 */
public class Collections {
    public static class DeviceCollection extends ResultCollection<Device>{}
    public static class CreditCardCollection extends ResultCollection<CreditCard>{}
    public static class TransactionCollection extends ResultCollection<Transaction>{}
    public static class CommitsCollection extends ResultCollection<Commit>{}
}
