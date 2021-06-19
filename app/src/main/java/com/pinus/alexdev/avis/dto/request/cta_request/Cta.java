package com.pinus.alexdev.avis.dto.request.cta_request;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Cta {
    private String defaultLocale;
    private String name;
    private List<OptionsRequest> options;
    private Map<String, Object> message;
    private Map<String, Object> question;

    public Cta(String defaultLocale, String name, List<OptionsRequest> options, Map<String, Object> message, Map<String, Object> question) {
        this.defaultLocale = defaultLocale;
        this.name = name;
        this.options = options;
        this.message = message;
        this.question = question;
    }
}
