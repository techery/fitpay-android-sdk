package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.api.models.device.Commit;

/**
 * Created by ssteveli on 6/3/16.
 */
public class CommitSkipped extends AbstractCommitNotProcessed {
    public CommitSkipped(Commit commit) {
        super(commit);
    }
}
