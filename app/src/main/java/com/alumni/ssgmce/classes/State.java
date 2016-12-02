package com.alumni.ssgmce.classes;

/**
 * Created by USer on 16-10-2016.
 */

public class State {

    String countrycode,stateName,stateId;

    public State(String countrycode, String stateName, String stateId) {
        this.countrycode = countrycode;
        this.stateName = stateName;
        this.stateId = stateId;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public String getStateName() {
        return stateName;
    }

    public String getStateId() {
        return stateId;
    }
}
