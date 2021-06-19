package com.pinus.alexdev.avis.model;

import java.util.List;

public class CTA_QRModel {
    private String QRName;
    private List<AnswerModel> answerModels;
    private long branchId;

    public String getQRName() {
        return QRName;
    }

    public void setQRName(String QRName) {
        this.QRName = QRName;
    }

    public List<AnswerModel> getAnswerModels() {
        return answerModels;
    }

    public void setAnswerModels(List<AnswerModel> answerModels) {
        this.answerModels = answerModels;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }
}
