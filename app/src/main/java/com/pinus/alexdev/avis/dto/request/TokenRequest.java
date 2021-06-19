package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class TokenRequest {
    private String androidId;
    private String os;
    private int daysToExpire;
    private boolean ctaState;
    private boolean chatState;
    private boolean reviewState;
    private boolean surveyState;
    private String token;

    public TokenRequest(String androidId, String os, int daysToExpire, boolean ctaState, boolean chatState, boolean reviewState, boolean surveyState, String token) {
        this.androidId = androidId;
        this.os = os;
        this.daysToExpire = daysToExpire;
        this.ctaState = ctaState;
        this.chatState = chatState;
        this.reviewState = reviewState;
        this.surveyState = surveyState;
        this.token = token;
    }

    public TokenRequest(boolean ctaState, boolean chatState, boolean reviewState, boolean surveyState, int daysToExpire) {
        this.ctaState = ctaState;
        this.chatState = chatState;
        this.reviewState = reviewState;
        this.surveyState = surveyState;
        this.daysToExpire = daysToExpire;
    }
}
