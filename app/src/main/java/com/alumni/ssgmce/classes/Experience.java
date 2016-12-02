package com.alumni.ssgmce.classes;

/**
 * Created by USer on 19-10-2016.
 */

public class Experience {

    String ExpId,Organization,Designation,DesignationLevel,Sector,Location,FromMonth,FromYear,ToMonth,ToYear, technology;


    public Experience(String expId, String organization, String designation, String designationLevel, String sector, String location, String fromMonth, String fromYear, String toMonth, String toYear, String technology) {
        ExpId = expId;
        Organization = organization;
        Designation = designation;
        DesignationLevel = designationLevel;
        Sector = sector;
        Location = location;
        FromMonth = fromMonth;
        FromYear = fromYear;
        ToMonth = toMonth;
        ToYear = toYear;
        this.technology = technology;
    }


    public String getTechnology() {
        return technology;
    }

    public String getFromMonth() {
        return FromMonth;
    }

    public String getFromYear() {
        return FromYear;
    }

    public String getToMonth() {
        return ToMonth;
    }

    public String getToYear() {
        return ToYear;
    }

    public String getExpId() {
        return ExpId;
    }

    public String getOrganization() {
        return Organization;
    }

    public String getDesignation() {
        return Designation;
    }

    public String getDesignationLevel() {
        return DesignationLevel;
    }

    public String getSector() {
        return Sector;
    }

    public String getLocation() {
        return Location;
    }
}
