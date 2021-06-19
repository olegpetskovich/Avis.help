package com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response;

import lombok.Data;

@Data
public class ValueResponse {
    private String ru;
    private String en;
    private String fr;
    private String ukr;

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getUkr() {
        return ukr;
    }

    public void setUkr(String ukr) {
        this.ukr = ukr;
    }
}
