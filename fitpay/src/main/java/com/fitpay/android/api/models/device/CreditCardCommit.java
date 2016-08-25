package com.fitpay.android.api.models.device;

import com.fitpay.android.api.enums.CardInitiators;
import com.fitpay.android.api.models.AssetReference;
import com.fitpay.android.api.models.card.Address;
import com.fitpay.android.api.models.card.CardMetaData;
import com.fitpay.android.api.models.card.VerificationMethod;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Commit API credit card model
 */
public class CreditCardCommit {

    protected String creditCardId;
    protected String userId;
    @SerializedName("default")
    protected Boolean defaultX;
    protected Long createdTsEpoch;
    protected Long lastModifiedTsEpoch;
    protected String state;
    @CardInitiators.Initiator
    protected String causedBy;
    protected String cardType;
    protected CardMetaData cardMetaData;
    protected String targetDeviceId;
    protected String targetDeviceType;
    protected String externalTokenReference;
//    protected List<VerificationMethod> verificationMethods;
    protected String termsAssetId;
    protected Long eligibilityExpirationEpoch;
    protected List<AssetReference> termsAssetReferences;

    //TODO eliminate the duplicates

    private String pan;
    private int expMonth;
    private int expYear;
    private String cvv;
    private String name;
    private Address address;
    private List<Device> deviceRelationships;


    //TODO resolve with above

    protected CreditCardCommit(){
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isDefault() {
        if (null == defaultX) {
            return false;
        }
        return defaultX.booleanValue();
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public long getLastModifiedTsEpoch() {
        return lastModifiedTsEpoch;
    }

    public String getState() {
        return state;
    }

    public String getCausedBy() {
        return causedBy;
    }

    public String getCardType() {
        return cardType;
    }

    public CardMetaData getCardMetaData() {
        return cardMetaData;
    }

    public String getTargetDeviceId() {
        return targetDeviceId;
    }

    public String getTargetDeviceType() {
        return targetDeviceType;
    }

    public String getExternalTokenReference() {
        return externalTokenReference;
    }

//    public List<VerificationMethod> getVerificationMethods() {
//        return verificationMethods;
//    }

    public Address getAddress() {
        return address;
    }

    public String getCvv() {
        return cvv;
    }

    public List<Device> getDeviceRelationships() {
        return deviceRelationships;
    }

    public Long getEligibilityExpirationEpoch() {
        return eligibilityExpirationEpoch;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public String getName() {
        return name;
    }

    public String getPan() {
        return pan;
    }

    public String getTermsAssetId() {
        return termsAssetId;
    }

    public List<AssetReference> getTermsAssetReferences() {
        return termsAssetReferences;
    }
}
