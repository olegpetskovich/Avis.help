package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class TokenResponse {
    private String notificationOs;
    private String expired;
    private String state;
    private String androidId;
    private String token;
    private String expirationDate;
}
