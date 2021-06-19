package com.pinus.alexdev.avis.dto.response.login_response;

public class UiPermissionsResponse {
    private boolean cta;
    private boolean statistic;
    private boolean landing;
    private boolean promoCodes;
    private boolean review;
    private boolean chats;
    private boolean scanner;
    private boolean survey;
    private boolean company;
    private boolean billing;

    public boolean isCta() {
        return cta;
    }

    public void setCta(boolean cta) {
        this.cta = cta;
    }

    public boolean isStatistic() {
        return statistic;
    }

    public void setStatistic(boolean statistic) {
        this.statistic = statistic;
    }

    public boolean isLanding() {
        return landing;
    }

    public void setLanding(boolean landing) {
        this.landing = landing;
    }

    public boolean isPromoCodes() {
        return promoCodes;
    }

    public void setPromoCodes(boolean promoCodes) {
        this.promoCodes = promoCodes;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public boolean isChats() {
        return chats;
    }

    public void setChats(boolean chats) {
        this.chats = chats;
    }

    public boolean isScanner() {
        return scanner;
    }

    public void setScanner(boolean scanner) {
        this.scanner = scanner;
    }

    public boolean isSurvey() {
        return survey;
    }

    public void setSurvey(boolean survey) {
        this.survey = survey;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }

    public boolean isBilling() {
        return billing;
    }

    public void setBilling(boolean billing) {
        this.billing = billing;
    }
}
