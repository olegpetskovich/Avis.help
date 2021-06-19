package com.pinus.alexdev.avis.dto.response.promo_code_response;

import lombok.Data;

@Data
public class ProceedPromoResponse {
    private String iconId;
    private QrResponse qr;
    private String timesUsedMax;
    private String usedOut;
    private String description;
    private String creationDate;
    private String type;
    private String expired;
    private String name;
    private String timesUsed;
    private String id;
    private String lifeTime;
    private String expirationDate;
}
