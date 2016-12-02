package com.alumni.ssgmce.classes;

/**
 * Created by USer on 16-10-2016.
 */

public class Branch {

    String BranchId,Branch;

    public Branch(String branchId, String branch) {
        BranchId = branchId;
        Branch = branch;
    }

    public String getBranchId() {
        return BranchId;
    }

    public String getBranch() {
        return Branch;
    }
}
