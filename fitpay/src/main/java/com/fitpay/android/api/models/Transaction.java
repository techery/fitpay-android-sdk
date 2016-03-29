package com.fitpay.android.api.models;


import android.os.Parcel;
import android.os.Parcelable;

public final class Transaction extends BaseModel implements Parcelable {

    private String transactionId;
    private String transactionType;
    private double amount;
    private String currencyCode;
    private String authorizationStatus;
    private long transactionTimeEpoch;
    private String merchantName;
    private String merchantCode;
    private String merchantType;

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public long getTransactionTimeEpoch() {
        return transactionTimeEpoch;
    }

    public String getErchantName() {
        return merchantName;
    }

    public String getErchantCode() {
        return merchantCode;
    }

    public String getErchantType() {
        return merchantType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionId);
        dest.writeString(this.transactionType);
        dest.writeDouble(this.amount);
        dest.writeString(this.currencyCode);
        dest.writeString(this.authorizationStatus);
        dest.writeLong(this.transactionTimeEpoch);
        dest.writeString(this.merchantName);
        dest.writeString(this.merchantCode);
        dest.writeString(this.merchantType);
        dest.writeParcelable(this.links, flags);
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        this.transactionId = in.readString();
        this.transactionType = in.readString();
        this.amount = in.readDouble();
        this.currencyCode = in.readString();
        this.authorizationStatus = in.readString();
        this.transactionTimeEpoch = in.readLong();
        this.merchantName = in.readString();
        this.merchantCode = in.readString();
        this.merchantType = in.readString();
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
