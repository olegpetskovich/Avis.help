package com.pinus.alexdev.avis.model;

import com.pinus.alexdev.avis.dto.response.ReviewStats;
import com.pinus.alexdev.avis.enums.MessageType;

import lombok.Data;


@Data
public class ChatMessage {
    private ReviewStats reviewStats;
    private MessageType messageType;
    private String content;
    private String sender;
    private Long reviewId;
    private Long promoId;

    public ChatMessage(MessageType messageType, String sender, Long reviewId) {
        this.messageType = messageType;
        this.sender = sender;
        this.reviewId = reviewId;
    }

    public ReviewStats getReviewStats() {
        return reviewStats;
    }

    public void setReviewStats(ReviewStats reviewStats) {
        this.reviewStats = reviewStats;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getPromoId() {
        return promoId;
    }

    public void setPromoId(Long promoId) {
        this.promoId = promoId;
    }
}
