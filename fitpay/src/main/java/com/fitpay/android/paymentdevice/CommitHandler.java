package com.fitpay.android.paymentdevice;

import com.fitpay.android.api.models.device.Commit;

/**
 * Created by tgs on 5/15/16.
 */
public interface CommitHandler {

    void processCommit(Commit commit);

}
