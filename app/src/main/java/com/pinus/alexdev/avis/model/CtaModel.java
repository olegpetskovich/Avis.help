package com.pinus.alexdev.avis.model;

import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.OptionsResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.QrCodeResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.QuestionResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.MessageResponse;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class CtaModel extends QrModel {
    private int id;
    private String defaultLocale;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuestionResponse getQuestion() {
        return question;
    }

    public void setQuestion(QuestionResponse question) {
        this.question = question;
    }

    public List<OptionsResponse> getOptions() {
        return options;
    }

    public void setOptions(List<OptionsResponse> options) {
        this.options = options;
    }

    public QrCodeResponse getQrCode() {
        return qrCode;
    }

    public void setQrCode(QrCodeResponse qrCode) {
        this.qrCode = qrCode;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MessageResponse getMessage() {
        return message;
    }

    public void setMessage(MessageResponse message) {
        this.message = message;
    }

    public boolean isAskNps() {
        return askNps;
    }

    public void setAskNps(boolean askNps) {
        this.askNps = askNps;
    }

    private String name;
    private QuestionResponse question;
    private List<OptionsResponse> options;
    private QrCodeResponse qrCode;
    private int branchId;
    private String category;
    private MessageResponse message;
    private boolean askNps;
}
