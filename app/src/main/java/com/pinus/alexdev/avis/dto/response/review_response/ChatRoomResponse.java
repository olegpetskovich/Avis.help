package com.pinus.alexdev.avis.dto.response.review_response;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChatRoomResponse implements Serializable {
    private boolean hasNewMessages;
    private String lastTimeAnsweredByManager;
    private String lastTimeMessageSent;
    private String id;
    private String lastTimeViewedByManager;
    private String type;

    public boolean isHasNewMessages() {
        return hasNewMessages;
    }

    public void setHasNewMessages(boolean hasNewMessages) {
        this.hasNewMessages = hasNewMessages;
    }

    public String getLastTimeAnsweredByManager() {
        return lastTimeAnsweredByManager;
    }

    public void setLastTimeAnsweredByManager(String lastTimeAnsweredByManager) {
        this.lastTimeAnsweredByManager = lastTimeAnsweredByManager;
    }

    public String getLastTimeMessageSent() {
        return lastTimeMessageSent;
    }

    public void setLastTimeMessageSent(String lastTimeMessageSent) {
        this.lastTimeMessageSent = lastTimeMessageSent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastTimeViewedByManager() {
        return lastTimeViewedByManager;
    }

    public void setLastTimeViewedByManager(String lastTimeViewedByManager) {
        this.lastTimeViewedByManager = lastTimeViewedByManager;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
