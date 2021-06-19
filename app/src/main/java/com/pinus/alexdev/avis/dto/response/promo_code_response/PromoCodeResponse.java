package com.pinus.alexdev.avis.dto.response.promo_code_response;

import lombok.Data;

@Data
public class PromoCodeResponse {
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

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public QrResponse getQr() {
        return qr;
    }

    public void setQr(QrResponse qr) {
        this.qr = qr;
    }

    public String getTimesUsedMax() {
        return timesUsedMax;
    }

    public void setTimesUsedMax(String timesUsedMax) {
        this.timesUsedMax = timesUsedMax;
    }

    public String getUsedOut() {
        return usedOut;
    }

    public void setUsedOut(String usedOut) {
        this.usedOut = usedOut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(String timesUsed) {
        this.timesUsed = timesUsed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(String lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
