package com.alumni.ssgmce.classes;

/**
 * Created by USer on 19-10-2016.
 */

public class Education {

    String EduId,College,Course,Branch,Location,FromMonth,FromYear,ToMonth,ToYear;

    public Education(String eduId, String college, String course, String branch, String location, String fromMonth, String fromYear, String toMonth, String toYear) {
        EduId = eduId;
        College = college;
        Course = course;
        Branch = branch;
        Location = location;
        FromMonth = fromMonth;
        FromYear = fromYear;
        ToMonth = toMonth;
        ToYear = toYear;
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

    public String getEduId() {
        return EduId;
    }

    public String getCollege() {
        return College;
    }

    public String getCourse() {
        return Course;
    }

    public String getBranch() {
        return Branch;
    }

    public String getLocation() {
        return Location;
    }
}
