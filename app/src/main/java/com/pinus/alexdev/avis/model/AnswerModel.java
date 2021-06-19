package com.pinus.alexdev.avis.model;

public class AnswerModel {
    private String key; // - это поле необходимо только при добавлении нового CTA в Qr Manager
    private String answerName;
    private String questionText;
    private String clicked;
    private String reactionTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

    public String getClickedCount() {
        return clicked;
    }

    public void setClickedCount(String clicked) {
        this.clicked = clicked;
    }

    public String getReactionTime() {
        return reactionTime;
    }

    public void setAverageReactionTime(String reactionTime) {
        this.reactionTime = reactionTime;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
