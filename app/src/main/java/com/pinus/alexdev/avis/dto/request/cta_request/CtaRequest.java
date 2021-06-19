package com.pinus.alexdev.avis.dto.request.cta_request;

public class CtaRequest {
    private long[] branchIdList;
    private Cta cta;

    public CtaRequest(long[] branchIdList, Cta cta) {
        this.branchIdList = branchIdList;
        this.cta = cta;
    }
}
