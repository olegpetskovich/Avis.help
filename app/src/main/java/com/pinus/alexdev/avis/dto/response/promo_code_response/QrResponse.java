package com.pinus.alexdev.avis.dto.response.promo_code_response;

import lombok.Data;

@Data
class QrResponse {
    private String imageUrl;
    private String id;
    private String humanReadableId;
    private String content;
}
