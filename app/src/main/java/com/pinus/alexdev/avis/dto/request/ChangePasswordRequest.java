package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
    private String oldPassword;

    public ChangePasswordRequest(String newPassword, String oldPassword) {
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }
}
