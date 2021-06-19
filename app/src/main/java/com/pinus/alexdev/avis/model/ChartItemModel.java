package com.pinus.alexdev.avis.model;

import lombok.Data;

@Data
public class ChartItemModel {
    private String name;
    private long opinionId; // - это поле используется для списка Opinion айтемов, для категорий используется только поле name

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(long opinionId) {
        this.opinionId = opinionId;
    }
}
