package com.pinus.alexdev.avis.dto.request;

import com.pinus.alexdev.avis.dto.request.cta_request.OptionsRequest;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CtaUpdateRequest {
    private String defaultLocale;
    private String name;
    private List<OptionsRequest> options;
    private Map<String, Object> message;
    private Map<String, Object> question;

    public CtaUpdateRequest(String defaultLocale, String name, List<OptionsRequest> options, Map<String, Object> message, Map<String, Object> question) {
        this.defaultLocale = defaultLocale;
        this.name = name;
        this.options = options;
        this.message = message;
        this.question = question;
    }
}
