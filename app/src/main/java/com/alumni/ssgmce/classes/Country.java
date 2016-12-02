package com.alumni.ssgmce.classes;

/**
 * Created by USer on 16-10-2016.
 */

public class Country {
    String ID,countryName,countrycode;

    public Country(String ID, String countryName, String countrycode) {
        this.ID = ID;
        this.countryName = countryName;
        this.countrycode = countrycode;
    }

    public String getID() {
        return ID;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountrycode() {
        return countrycode;
    }
}
