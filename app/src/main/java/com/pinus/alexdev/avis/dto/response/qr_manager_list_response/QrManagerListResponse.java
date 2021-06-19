package com.pinus.alexdev.avis.dto.response.qr_manager_list_response;

import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.CTAResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.OpinionResponse;

import java.util.List;

import lombok.Data;

@Data
public class QrManagerListResponse {
    private List<CTAResponse> cta;
    private List<OpinionResponse> opinion;

    public List<CTAResponse> getCta() {
        return cta;
    }

    public void setCta(List<CTAResponse> cta) {
        this.cta = cta;
    }

    public List<OpinionResponse> getOpinion() {
        return opinion;
    }

    public void setOpinion(List<OpinionResponse> opinion) {
        this.opinion = opinion;
    }
}
