package com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response;

import lombok.Data;

@Data
public class OptionsResponse {
    private String key;
    private int hits;
    private long averageReactionTime;
    private ValueResponse value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public long getAverageReactionTime() {
        return averageReactionTime;
    }

    public void setAverageReactionTime(long averageReactionTime) {
        this.averageReactionTime = averageReactionTime;
    }

    public ValueResponse getValue() {
        return value;
    }

    public void setValue(ValueResponse value) {
        this.value = value;
    }
}
