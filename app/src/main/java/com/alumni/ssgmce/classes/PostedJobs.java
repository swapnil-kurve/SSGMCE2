package com.alumni.ssgmce.classes;

/**
 * Created by USer on 12-10-2016.
 */

public class PostedJobs {

    String JobId,JobTitle,MinExperience,MaxExperience,Location,InsertDate,lastDate, JobDescription,Attachement,OrganizationId,Organization,Branch,regLink,technology;

    public PostedJobs(String jobId, String jobTitle, String minExperience, String maxExperience, String location, String insertDate, String lastDate, String jobDescription, String attachement, String organizationId, String organization, String branch, String regLink, String technology) {
        JobId = jobId;
        JobTitle = jobTitle;
        MinExperience = minExperience;
        MaxExperience = maxExperience;
        Location = location;
        InsertDate = insertDate;
        this.lastDate = lastDate;
        JobDescription = jobDescription;
        Attachement = attachement;
        OrganizationId = organizationId;
        Organization = organization;
        Branch = branch;
        this.regLink = regLink;
        this.technology = technology;
    }

    public String getJobDescription() {
        return JobDescription;
    }

    public String getAttachement() {
        return Attachement;
    }

    public String getOrganizationId() {
        return OrganizationId;
    }

    public String getOrganization() {
        return Organization;
    }

    public String getBranch() {
        return Branch;
    }

    public String getRegLink() {
        return regLink;
    }

    public String getTechnology() {
        return technology;
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

    public String getInsertDate() {
        return InsertDate;
    }

    public String getLastDate() {
        return lastDate;
    }
}
