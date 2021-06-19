package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class OrganizationRequest {
    private String name;

    public OrganizationRequest(String name) {
        this.name = name;
    }
}
