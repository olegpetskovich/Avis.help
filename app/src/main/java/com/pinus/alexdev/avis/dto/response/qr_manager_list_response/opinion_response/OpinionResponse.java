package com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response;

import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.QrCodeResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.QuestionResponse;
import com.pinus.alexdev.avis.model.QrModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class OpinionResponse extends QrModel {
    private String category;
    private String defaultLocale;
    private int id;
    private String name;
    private MessageResponse message;
    private QuestionResponse question;
    private QrCodeResponse qrCode;
    private boolean askNps;
    private int branchId;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageResponse getMessage() {
        return message;
    }

    public void setMessage(MessageResponse message) {
        this.message = message;
    }

    public QuestionResponse getQuestion() {
        return question;
    }

    public void setQuestion(QuestionResponse question) {
        this.question = question;
    }

    public QrCodeResponse getQrCode() {
        return qrCode;
    }

    public void setQrCode(QrCodeResponse qrCode) {
        this.qrCode = qrCode;
    }

    public boolean isAskNps() {
        return askNps;
    }

    public void setAskNps(boolean askNps) {
        this.askNps = askNps;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
}
