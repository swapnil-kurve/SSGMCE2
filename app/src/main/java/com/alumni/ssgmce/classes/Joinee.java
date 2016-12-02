package com.alumni.ssgmce.classes;

/**
 * Created by USer on 10-10-2016.
 */

public class Joinee {
    String memberID,fullName,Date,monthYear,signupmembers;

    public Joinee(String memberID, String fullName, String date, String monthYear, String signupmembers) {
        this.memberID = memberID;
        this.fullName = fullName;
        Date = date;
        this.monthYear = monthYear;
        this.signupmembers = signupmembers;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDate() {
        return Date;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public String getSignupmembers() {
        return signupmembers;
    }
}
