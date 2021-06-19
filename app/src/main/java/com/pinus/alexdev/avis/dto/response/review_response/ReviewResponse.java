package com.pinus.alexdev.avis.dto.response.review_response;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReviewResponse implements Serializable {
    private String defaultLocale;
    private long branchId;
    private long opinionId;
    private String opinionName; // - Может быть объектом
    private String opinionCategoty;
    private ReviewResp review;
    private String branchName;

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public long getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(long opinionId) {
        this.opinionId = opinionId;
    }

    public String getOpinionName() {
        return opinionName;
    }

    public void setOpinionName(String opinionName) {
        this.opinionName = opinionName;
    }

    public String getOpinionCategoty() {
        return opinionCategoty;
    }

    public void setOpinionCategoty(String opinionCategoty) {
        this.opinionCategoty = opinionCategoty;
    }

    public ReviewResp getReview() {
        return review;
    }

    public void setReview(ReviewResp review) {
        this.review = review;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
