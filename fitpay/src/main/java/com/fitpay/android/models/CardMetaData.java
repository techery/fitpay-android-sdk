package com.fitpay.android.models;

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
    private List<?> brandLogo;
    private List<?> cardBackground;
    private List<?> cardBackgroundCombined;
    private List<?> icon;
    private List<?> issuerLogo;


    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setTermsAndConditionsUrl(String termsAndConditionsUrl) {
        this.termsAndConditionsUrl = termsAndConditionsUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public void setBrandLogo(List<?> brandLogo) {
        this.brandLogo = brandLogo;
    }

    public void setCardBackground(List<?> cardBackground) {
        this.cardBackground = cardBackground;
    }

    public void setCardBackgroundCombined(List<?> cardBackgroundCombined) {
        this.cardBackgroundCombined = cardBackgroundCombined;
    }

    public void setIcon(List<?> icon) {
        this.icon = icon;
    }

    public void setIssuerLogo(List<?> issuerLogo) {
        this.issuerLogo = issuerLogo;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getTermsAndConditionsUrl() {
        return termsAndConditionsUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public List<?> getBrandLogo() {
        return brandLogo;
    }

    public List<?> getCardBackground() {
        return cardBackground;
    }

    public List<?> getCardBackgroundCombined() {
        return cardBackgroundCombined;
    }

    public List<?> getIcon() {
        return icon;
    }

    public List<?> getIssuerLogo() {
        return issuerLogo;
    }
}