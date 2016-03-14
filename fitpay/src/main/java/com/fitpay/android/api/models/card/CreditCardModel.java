package com.fitpay.android.api.models.card;

import com.fitpay.android.api.enums.CardInitiators;
import com.fitpay.android.api.models.AssetReference;
import com.fitpay.android.api.models.BaseModel;
import com.fitpay.android.api.models.Device;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Credit card model
 */
abstract class CreditCardModel extends BaseModel {

    protected String creditCardId;
    protected String userId;
    @SerializedName("default")
    protected Boolean defaultX;
    protected String createdTs;
    protected Long createdTsEpoch;
    protected String lastModifiedTs;
    protected Long lastModifiedTsEpoch;
    protected String state;
    @CardInitiators.Initiator
    protected String causedBy;
    protected String cardType;
    protected CardMetaData cardMetaData;
    protected String targetDeviceId;
    protected String targetDeviceType;
    protected String externalTokenReference;
    protected List<VerificationMethod> verificationMethods;
    protected List<Device> deviceRelationships;
    @SerializedName("encryptedData")
    protected CreditCardInfo creditCardInfo;
    protected String termsAssetId;
    protected String eligibilityExpiration;
    protected Long eligibilityExpirationEpoch;
    protected List<AssetReference> termsAssetReferences;

    protected CreditCardModel(){
        creditCardInfo = new CreditCardInfo();
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isDefault() {
        return defaultX;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getLastModifiedTs() {
        return lastModifiedTs;
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

    public List<VerificationMethod> getVerificationMethods() {
        return verificationMethods;
    }

    public List<Device> getDeviceRelationships() {
        return deviceRelationships;
    }

    public CreditCardInfo getCreditCardInfo() {
        return creditCardInfo;
    }
}
