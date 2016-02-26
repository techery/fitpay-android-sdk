package com.fitpay.android.api.models;

import java.util.List;

public class CardMetaData {

    private String labelColor;
    private String issuerName;
    private String shortDescription;
    private String longDescription;
    private String contactUrl;
    private String contactPhone;
    private String contactEmail;
    private String termsAndConditionsUrl;
    private String privacyPolicyUrl;
    private List<ImageAssetReference> brandLogo;
    private List<ImageAssetReference> cardBackground;
    private List<ImageAssetReference> cardBackgroundCombined;

    //TODO: check values
    private List<ImageAssetReference> icon;
    private List<ImageAssetReference> issuerLogo;

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getTermsAndConditionsUrl() {
        return termsAndConditionsUrl;
    }

    public void setTermsAndConditionsUrl(String termsAndConditionsUrl) {
        this.termsAndConditionsUrl = termsAndConditionsUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public List<ImageAssetReference> getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(List<ImageAssetReference> brandLogo) {
        this.brandLogo = brandLogo;
    }

    public List<ImageAssetReference> getCardBackground() {
        return cardBackground;
    }

    public void setCardBackground(List<ImageAssetReference> cardBackground) {
        this.cardBackground = cardBackground;
    }

    public List<ImageAssetReference> getCardBackgroundCombined() {
        return cardBackgroundCombined;
    }

    public void setCardBackgroundCombined(List<ImageAssetReference> cardBackgroundCombined) {
        this.cardBackgroundCombined = cardBackgroundCombined;
    }

    public List<ImageAssetReference> getIcon() {
        return icon;
    }

    public void setIcon(List<ImageAssetReference> icon) {
        this.icon = icon;
    }

    public List<ImageAssetReference> getIssuerLogo() {
        return issuerLogo;
    }

    public void setIssuerLogo(List<ImageAssetReference> issuerLogo) {
        this.issuerLogo = issuerLogo;
    }
}