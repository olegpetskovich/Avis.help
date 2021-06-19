package com.pinus.alexdev.avis.enums;

public enum RangeStatisticEnum {
    DAY(0), WEEK(-7), MONTH(-30), YEAR(-365);

    private int value;

    public int getValue() {
        return value;
    }
    RangeStatisticEnum(int value) {
        this.value = value;
    }
}
