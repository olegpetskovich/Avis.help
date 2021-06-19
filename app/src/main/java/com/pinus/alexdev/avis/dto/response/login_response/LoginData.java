package com.pinus.alexdev.avis.dto.response.login_response;

import com.pinus.alexdev.avis.dto.response.BranchesItem;

import java.util.List;

import lombok.Data;

@Data
public class LoginData {
    private String organizationId;
    private List<BranchesItem> branches;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public List<BranchesItem> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchesItem> branches) {
        this.branches = branches;
    }
}
