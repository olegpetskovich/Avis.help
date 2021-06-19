package com.pinus.alexdev.avis.dto.response;

import lombok.Data;

@Data
public class NPSResponse {
    private String nps;
    private String detractor;
    private String neutral;
    private String promoters;
    private String from;
    private String to;

    public String getNps() {
        return nps;
    }

    public void setNps(String nps) {
        this.nps = nps;
    }

    public String getDetractor() {
        return detractor;
    }

    public void setDetractor(String detractor) {
        this.detractor = detractor;
    }

    public String getNeutral() {
        return neutral;
    }

    public void setNeutral(String neutral) {
        this.neutral = neutral;
    }

    public String getPromoters() {
        return promoters;
    }

    public void setPromoters(String promoters) {
        this.promoters = promoters;
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
}
