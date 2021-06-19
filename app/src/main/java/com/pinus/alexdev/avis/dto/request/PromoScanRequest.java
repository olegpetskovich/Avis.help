package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class PromoScanRequest {
    private int organizationId;
    private String promoContent;

    public PromoScanRequest(int organizationId, String promoContent) {
        this.organizationId = organizationId;
        this.promoContent = promoContent;
    }
}
