package com.pinus.alexdev.avis.dto.request.opinion_request;

import lombok.Data;

@Data
public class OpinionRequest {
    private long[] branchIdList;
    private Opinion opinion;

    public OpinionRequest(long[] branchIdList, Opinion opinion) {
        this.branchIdList = branchIdList;
        this.opinion = opinion;
    }
}
