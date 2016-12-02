package com.alumni.ssgmce.classes;

/**
 * Created by USer on 10-10-2016.
 */

public class Groups {
    String GrpId,Groups,status;

    public Groups(String grpId, String groups, String status) {
        GrpId = grpId;
        Groups = groups;
        this.status = status;
    }

    public String getGrpId() {
        return GrpId;
    }

    public String getGroups() {
        return Groups;
    }

    public String getStatus() {
        return status;
    }
}
