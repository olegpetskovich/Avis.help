package com.pinus.alexdev.avis.dto.response.review_response;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReviewResp implements Serializable {
    private boolean chatOpened;
    private String lastSendTime;
    private String dateViewed;
    private String locale;
    private boolean isViewed;
    private String dateCreated;
    private String managerComment;
    private String phone;
    private String imageUrl;
    private boolean isAnswered;
    private boolean anonymous;
    private int impression;
    private String chatUrl;
    private String comment;
    private int id;
    private boolean isChatViewed;
    private String email;
    private ChatRoomResponse chatRoom;

    public boolean isChatOpened() {
        return chatOpened;
    }

    public void setChatOpened(boolean chatOpened) {
        this.chatOpened = chatOpened;
    }

    public String getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(String lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public String getDateViewed() {
        return dateViewed;
    }

    public void setDateViewed(String dateViewed) {
        this.dateViewed = dateViewed;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public int getImpression() {
        return impression;
    }

    public void setImpression(int impression) {
        this.impression = impression;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChatViewed() {
        return isChatViewed;
    }

    public void setChatViewed(boolean chatViewed) {
        isChatViewed = chatViewed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ChatRoomResponse getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoomResponse chatRoom) {
        this.chatRoom = chatRoom;
    }
}
