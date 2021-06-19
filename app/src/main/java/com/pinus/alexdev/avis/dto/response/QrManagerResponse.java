package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class QrManagerResponse {
    private String endpointType;
    private String endpointId;
    private String imageUrl;
    private String id;
    private String humanReadableId;
    private String free;
    private String content;
}
