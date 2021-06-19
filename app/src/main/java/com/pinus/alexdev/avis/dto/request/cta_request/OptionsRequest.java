package com.pinus.alexdev.avis.dto.request.cta_request;

import java.util.Map;

import lombok.Data;

@Data
public class OptionsRequest {
    private String key;
    private Map<String, String> value;

    public OptionsRequest(String key, Map<String, String> value) {
        this.key = key;
        this.value = value;
    }
}
