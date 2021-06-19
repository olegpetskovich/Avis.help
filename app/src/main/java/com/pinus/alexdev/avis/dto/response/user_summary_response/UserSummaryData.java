package com.pinus.alexdev.avis.dto.response.user_summary_response;

import lombok.Data;

@Data
public class UserSummaryData {
    private String organizationId;

    @Override
    public String toString() {
        return "ClassPojo [organizationId = " + organizationId + "]";
    }
}
