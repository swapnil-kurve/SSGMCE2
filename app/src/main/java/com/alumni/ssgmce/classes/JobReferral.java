package com.alumni.ssgmce.classes;

/**
 * Created by USer on 12-10-2016.
 */

public class JobReferral {
    String JobId,JobTitle,JobDescription,OrganizationId,Organization,MinExperience,MaxExperience,Attachement,Attachement1,IsActive,Location,AdminApproved,
            Branch,technology,lastDate,displayFile,InsertDate,MemberId,MemberApproved,IsDelete,regLink,membername;

    public JobReferral(String jobId, String jobTitle, String jobDescription, String organizationId, String organization, String minExperience, String maxExperience, String attachement, String attachement1, String isActive, String location, String adminApproved, String branch, String technology, String lastDate, String displayFile, String insertDate, String memberId, String memberApproved, String isDelete, String regLink, String membername) {
        JobId = jobId;
        JobTitle = jobTitle;
        JobDescription = jobDescription;
        OrganizationId = organizationId;
        Organization = organization;
        MinExperience = minExperience;
        MaxExperience = maxExperience;
        Attachement = attachement;
        Attachement1 = attachement1;
        IsActive = isActive;
        Location = location;
        AdminApproved = adminApproved;
        Branch = branch;
        this.technology = technology;
        this.lastDate = lastDate;
        this.displayFile = displayFile;
        InsertDate = insertDate;
        MemberId = memberId;
        MemberApproved = memberApproved;
        IsDelete = isDelete;
        this.regLink = regLink;
        this.membername = membername;
    }

    public String getJobId() {
        return JobId;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public String getJobDescription() {
        return JobDescription;
    }

    public String getOrganizationId() {
        return OrganizationId;
    }

    public String getOrganization() {
        return Organization;
    }

    public String getMinExperience() {
        return MinExperience;
    }

    public String getMaxExperience() {
        return MaxExperience;
    }

    public String getAttachement() {
        return Attachement;
    }

    public String getAttachement1() {
        return Attachement1;
    }

    public String getIsActive() {
        return IsActive;
    }

    public String getLocation() {
        return Location;
    }

    public String getAdminApproved() {
        return AdminApproved;
    }

    public String getBranch() {
        return Branch;
    }

    public String getTechnology() {
        return technology;
    }

    public String getLastDate() {
        return lastDate;
    }

    public String getDisplayFile() {
        return displayFile;
    }

    public String getInsertDate() {
        return InsertDate;
    }

    public String getMemberId() {
        return MemberId;
    }

    public String getMemberApproved() {
        return MemberApproved;
    }

    public String getIsDelete() {
        return IsDelete;
    }

    public String getRegLink() {
        return regLink;
    }

    public String getMembername() {
        return membername;
    }
}
