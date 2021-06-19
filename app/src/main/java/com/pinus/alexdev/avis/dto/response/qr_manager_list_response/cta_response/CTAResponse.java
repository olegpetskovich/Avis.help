package com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response;

import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.MessageResponse;
import com.pinus.alexdev.avis.model.QrModel;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class CTAResponse extends QrModel {
    private int id;

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

    public MessageResponse getMessage() {
        return message;
    }

    public void setMessage(MessageResponse message) {
        this.message = message;
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

    private String defaultLocale;
    private String name;
    private QuestionResponse question;
    private MessageResponse message;
    private List<OptionsResponse> options;
    private QrCodeResponse qrCode;
    private int branchId;
}
