package com.alumni.ssgmce.classes;

/**
 * Created by USer on 12-10-2016.
 */

public class AvailableJobs {
    String JobId,JobTitle,MinExperience,MaxExperience,Location,lastDate,MemberName,PostedDate, Attachement;

    public AvailableJobs(String jobId, String jobTitle, String minExperience, String maxExperience, String location, String lastDate, String memberName, String postedDate, String Attachement) {
        JobId = jobId;
        JobTitle = jobTitle;
        MinExperience = minExperience;
        MaxExperience = maxExperience;
        Location = location;
        this.lastDate = lastDate;
        MemberName = memberName;
        PostedDate = postedDate;
        this.Attachement = Attachement;
    }

    public String getAttachement() {
        return Attachement;
    }

    public String getJobId() {
        return JobId;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public String getMinExperience() {
        return MinExperience;
    }

    public String getMaxExperience() {
        return MaxExperience;
    }

    public String getLocation() {
        return Location;
    }

    public String getLastDate() {
        return lastDate;
    }

    public String getMemberName() {
        return MemberName;
    }

    public String getPostedDate() {
        return PostedDate;
    }
}
