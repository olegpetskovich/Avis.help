package com.pinus.alexdev.avis.model;

import com.pinus.alexdev.avis.enums.SenderChatEnums;

import lombok.Data;

@Data
public class ChatLocal {
    String message;
    SenderChatEnums sender;

    public ChatLocal(String message, SenderChatEnums i) {
        this.message = message;
        this.sender = i;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SenderChatEnums getSender() {
        return sender;
    }

    public void setSender(SenderChatEnums sender) {
        this.sender = sender;
    }
}
