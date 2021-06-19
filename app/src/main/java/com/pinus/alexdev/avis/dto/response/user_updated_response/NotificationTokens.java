package com.pinus.alexdev.avis.dto.response.user_updated_response;

import lombok.Data;

@Data
public class NotificationTokens {
    private String notificationOs;
    private String expired;
    private String id;
    private String state;
    private String token;
    private String expirationDate;

    public String getNotificationOs() {
        return notificationOs;
    }

    public void setNotificationOs(String notificationOs) {
        this.notificationOs = notificationOs;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "ClassPojo [notificationOs = " + notificationOs + ", expired = " + expired + ", id = " + id + ", state = " + state + ", token = " + token + ", expirationDate = " + expirationDate + "]";
    }
}
