package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class PromoSmsRequest {
    private String phoneNumber;

    public PromoSmsRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
