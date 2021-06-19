package com.pinus.alexdev.avis.dto.request.opinion_request;

import java.util.Map;

import lombok.Data;

@Data
public
class Opinion {
    private boolean askNps;
    private String category;
    private String defaultLocale;
    private String name;
    private Map<String, Object> message;
    private Map<String, Object> question;

    public Opinion(boolean askNps, String category, String defaultLocale, Map<String, Object> message, String name, Map<String, Object> question) {
        this.askNps = askNps;
        this.category = category;
        this.defaultLocale = defaultLocale;
        this.message = message;
        this.name = name;
        this.question = question;
    }
}
