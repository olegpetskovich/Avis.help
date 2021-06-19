package com.pinus.alexdev.avis.dto.response;

import com.pinus.alexdev.avis.dto.response.review_response.ChatRoomResponse;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConversationResponse implements Serializable {
    private String lastTimeAnsweredByManager;
    private boolean hasNewMessages;
    private String invitationPhoneNumber;
    private String lastTimeMessageSent;
    private String id;
    private int reviewId;
    private String chatUrl;
    private String branchName;
    private int impression;
    private String lastTimeViewedByManager;
    private String creationDate;
    private String chatType;
    private ChatRoomResponse chatRoom;

    public ChatRoomResponse getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoomResponse chatRoom) {
        this.chatRoom = chatRoom;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getImpression() {
        return impression;
    }

    public void setImpression(int impression) {
        this.impression = impression;
    }

    public String getLastTimeAnsweredByManager() {
        return lastTimeAnsweredByManager;
    }

    public void setLastTimeAnsweredByManager(String lastTimeAnsweredByManager) {
        this.lastTimeAnsweredByManager = lastTimeAnsweredByManager;
    }

    public String getInvitationPhoneNumber() {
        return invitationPhoneNumber;
    }

    public void setInvitationPhoneNumber(String invitationPhoneNumber) {
        this.invitationPhoneNumber = invitationPhoneNumber;
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public boolean isHasNewMessages() {
        return hasNewMessages;
    }

    public void setHasNewMessages(boolean hasNewMessages) {
        this.hasNewMessages = hasNewMessages;
    }
}
