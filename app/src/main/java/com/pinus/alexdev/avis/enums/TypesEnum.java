package com.pinus.alexdev.avis.enums;

import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.utils.App;

public enum TypesEnum {
    OVERALL(R.string.branches), BYID(0);

    private int value;

    public int getValue() {
        return value;
    }

    TypesEnum(int value) {
        this.value = value;
    }
}
