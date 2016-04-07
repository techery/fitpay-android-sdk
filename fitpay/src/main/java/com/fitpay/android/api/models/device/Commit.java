package com.fitpay.android.api.models.device;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;

public final class Commit extends CommitModel {
    public static final String PREVIOUS = "previous";

    /**
     * Get previous commit
     *
     * @param callback result callback
     */
    public void getPreviousCommit(@NonNull ApiCallback<Commit> callback) {
        makeGetCall(PREVIOUS, null, Commit.class, callback);
    }
}