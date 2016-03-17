package com.fitpay.android.api.models.card;

public final class Address {

    /**
     * description : The billing address street name and number
     */
    private String street1;

    /**
     * description : The billing address unit or suite number, if available
     */
    private String street2;

    /**
     * description : Additional billing address unit or suite number, if available
     */
    private String street3;

    /**
     * description : The billing address city
     */
    private String city;

    /**
     * description : The billing address state
     */
    private String state;

    /**
     * description : The billing address five-digit zip code
     */
    private String postalCode;

    /**
     * description : The billing address country code
     */
    private String countryCode;


    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getStreet3() {
        return street3;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
