package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class ChatByNumberRequest {
    private String locale;
    private String phoneNumber;

    public ChatByNumberRequest(String locale, String phoneNumber) {
        this.locale = locale;
        this.phoneNumber = phoneNumber;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
