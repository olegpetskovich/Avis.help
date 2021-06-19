package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class DeviceResponse {
    private long id;
    private String androidId;
    private String reviewState;
    private String chatState;
    private String ctaState;
    private String surveyState;
    private String token;
    private String notificationOs;
    private boolean expired;
    private String expirationDate;

    public String getNotificationOs() {
        return notificationOs;
    }

    public void setNotificationOs(String notificationOs) {
        this.notificationOs = notificationOs;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getReviewState() {
        return reviewState;
    }

    public void setReviewState(String reviewState) {
        this.reviewState = reviewState;
    }

    public String getChatState() {
        return chatState;
    }

    public void setChatState(String chatState) {
        this.chatState = chatState;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCtaState() {
        return ctaState;
    }

    public void setCtaState(String ctaState) {
        this.ctaState = ctaState;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getSurveyState() {
        return surveyState;
    }

    public void setSurveyState(String surveyState) {
        this.surveyState = surveyState;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
