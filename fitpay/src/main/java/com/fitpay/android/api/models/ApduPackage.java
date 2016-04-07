package com.fitpay.android.api.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public final class ApduPackage implements Parcelable {

    private String packageId;
    private String state;
    private String executedTs;
    private int executedDuration;
    private List<ApduResponses> apduResponses;

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setExecutedTs(String executedTs) {
        this.executedTs = executedTs;
    }

    public void setExecutedDuration(int executedDuration) {
        this.executedDuration = executedDuration;
    }

    public void setApduResponses(List<ApduResponses> apduResponses) {
        this.apduResponses = apduResponses;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getState() {
        return state;
    }

    public String getExecutedTs() {
        return executedTs;
    }

    public int getExecutedDuration() {
        return executedDuration;
    }

    public List<ApduResponses> getApduResponses() {
        return apduResponses;
    }

    public static class ApduResponses implements Parcelable {
        private String commandId;
        private String responseCode;
        private String responseData;

        public void setCommandId(String commandId) {
            this.commandId = commandId;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public void setResponseData(String responseData) {
            this.responseData = responseData;
        }

        public String getCommandId() {
            return commandId;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public String getResponseData() {
            return responseData;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.commandId);
            dest.writeString(this.responseCode);
            dest.writeString(this.responseData);
        }

        public ApduResponses() {
        }

        protected ApduResponses(Parcel in) {
            this.commandId = in.readString();
            this.responseCode = in.readString();
            this.responseData = in.readString();
        }

        public static final Creator<ApduResponses> CREATOR = new Creator<ApduResponses>() {
            @Override
            public ApduResponses createFromParcel(Parcel source) {
                return new ApduResponses(source);
            }

            @Override
            public ApduResponses[] newArray(int size) {
                return new ApduResponses[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageId);
        dest.writeString(this.state);
        dest.writeString(this.executedTs);
        dest.writeInt(this.executedDuration);
        dest.writeList(this.apduResponses);
    }

    public ApduPackage() {
    }

    protected ApduPackage(Parcel in) {
        this.packageId = in.readString();
        this.state = in.readString();
        this.executedTs = in.readString();
        this.executedDuration = in.readInt();
        this.apduResponses = new ArrayList<>();
        in.readList(this.apduResponses, ApduResponses.class.getClassLoader());
    }

    public static final Parcelable.Creator<ApduPackage> CREATOR = new Parcelable.Creator<ApduPackage>() {
        @Override
        public ApduPackage createFromParcel(Parcel source) {
            return new ApduPackage(source);
        }

        @Override
        public ApduPackage[] newArray(int size) {
            return new ApduPackage[size];
        }
    };
}
