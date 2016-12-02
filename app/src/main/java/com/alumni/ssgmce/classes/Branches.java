package com.alumni.ssgmce.classes;

/**
 * Created by USer on 12-10-2016.
 */

public class Branches {
    String CourseId,BranchId,Branch;

    public Branches(String courseId, String branchId, String branch) {
        CourseId = courseId;
        BranchId = branchId;
        Branch = branch;
    }

    public String getCourseId() {
        return CourseId;
    }

    public String getBranchId() {
        return BranchId;
    }

    public String getBranch() {
        return Branch;
    }
}
