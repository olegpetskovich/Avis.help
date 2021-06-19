package com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response;

import lombok.Data;

@Data
public
class QrCodeResponse {
    private String endpointType;
    private String endpointId;
    private String imageUrl;
    private String id;
    private String humanReadableId;
    private boolean free;
    private String content;

    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHumanReadableId() {
        return humanReadableId;
    }

    public void setHumanReadableId(String humanReadableId) {
        this.humanReadableId = humanReadableId;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
