package com.pinus.alexdev.avis.dto.response.branch_statistic_response;

import java.util.List;

import lombok.Data;

@Data
public class Review {
    private List<ReviewHourStatsItem> reviews;
    private String numberOfReviews;
    private String range;
    private String from;
    private String to;
    private String averageImpression;

    public List<ReviewHourStatsItem> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewHourStatsItem> reviews) {
        this.reviews = reviews;
    }

    public String getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(String numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAverageImpression() {
        return averageImpression;
    }

    public void setAverageImpression(String averageImpression) {
        this.averageImpression = averageImpression;
    }
}
