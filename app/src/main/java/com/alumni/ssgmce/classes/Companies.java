package com.alumni.ssgmce.classes;

/**
 * Created by USer on 13-10-2016.
 */

public class Companies {
    String OrganizationId,Organization;

    public Companies(String organizationId, String organization) {
        OrganizationId = organizationId;
        Organization = organization;
    }

    public String getOrganizationId() {
        return OrganizationId;
    }

    public String getOrganization() {
        return Organization;
    }
}
