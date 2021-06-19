package com.pinus.alexdev.avis.dto.request;

import java.util.Map;

import lombok.Data;

@Data
public class OpinionUpdateRequest {
    private boolean askNps;
    private String category;
    private String defaultLocale;
    private String name;
    private Map<String, Object> message;
    private Map<String, Object> question;

    public OpinionUpdateRequest(boolean askNps, String category, String defaultLocale, String name, Map<String, Object> message, Map<String, Object> question) {
        this.askNps = askNps;
        this.category = category;
        this.defaultLocale = defaultLocale;
        this.name = name;
        this.message = message;
        this.question = question;
    }
}
