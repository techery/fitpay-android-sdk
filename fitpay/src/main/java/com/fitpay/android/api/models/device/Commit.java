package com.fitpay.android.api.models.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Payload;

public final class Commit extends CommitModel implements Parcelable {
    public static final String PREVIOUS = "previous";

    /**
     * Get previous commit
     *
     * @param callback result callback
     */
    public void getPreviousCommit(@NonNull ApiCallback<Commit> callback) {
        makeGetCall(PREVIOUS, null, Commit.class, callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commitId);
        dest.writeString(this.previousCommitId);
        dest.writeString(this.commitType);
        dest.writeValue(this.createdTs);
        dest.writeParcelable(this.payload, flags);
        dest.writeParcelable(this.links, flags);
    }

    public Commit() {
    }

    protected Commit(Parcel in) {
        this.commitId = in.readString();
        this.previousCommitId = in.readString();
        @CommitTypes.Type String ct = in.readString();
        this.commitType = ct;
        this.createdTs = (Long) in.readValue(Long.class.getClassLoader());
        this.payload = in.readParcelable(Payload.class.getClassLoader());
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<Commit> CREATOR = new Parcelable.Creator<Commit>() {
        @Override
        public Commit createFromParcel(Parcel source) {
            return new Commit(source);
        }

        @Override
        public Commit[] newArray(int size) {
            return new Commit[size];
        }
    };
}