package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.request.CtaUpdateRequest;
import com.pinus.alexdev.avis.dto.request.OpinionUpdateRequest;
import com.pinus.alexdev.avis.dto.request.cta_request.CtaRequest;
import com.pinus.alexdev.avis.dto.request.opinion_request.OpinionRequest;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.QrManagerListResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.CTAResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.OptionsResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.OpinionResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface QrManagerApiService {
    //Get QR List
    @GET("/api/v1/organization/{organizationId}/bundle/qr-type")
    Single<QrManagerListResponse> getOrganizationQrList(@Path("organizationId") int organizationId);

    //Get CTA List for separate CTA Activity
    @GET("/api/v1/organization/{organizationId}/bundle/cta")
    Single<List<CTAResponse>> getCTAs(@Path("organizationId") int organizationId);

    //Add QR
    @POST("/api/v1/organization/{organizationId}/bundle/cta")
    Single<String> addCtaQr(@Body CtaRequest ctaDto, @Path("organizationId") int organizationId);

    @POST("/api/v1/organization/{organizationId}/bundle/opinion")
    Single<String> addOpinionQr(@Body OpinionRequest opinionDto, @Path("organizationId") int organizationId);

    //Update QR
    @PATCH("/api/v1/organization/{organizationId}/branch/{branchId}/cta/{ctaId}")
    Single<CTAResponse> updateCtaQr(@Path("branchId") int branchId, @Body CtaUpdateRequest ctaDto, @Path("ctaId") int ctaId, @Path("organizationId") int organizationId);

    @PATCH("/api/v1/organization/{organizationId}/branch/{branchId}/opinion/{opinionId}")
    Single<OpinionResponse> updateOpinionQr(@Path("branchId") int branchId, @Body OpinionUpdateRequest opinionDto, @Path("opinionId") int opinionId, @Path("organizationId") int organizationId);

    //Delete QR
    @DELETE("/api/v1/organization/{organizationId}/branch/{branchId}/cta/{ctaId}")
    Single<String> deleteQrCta(@Path("branchId") int branchId, @Path("ctaId") int ctaId, @Path("organizationId") int organizationId);

    @DELETE("/api/v1/organization/{organizationId}/branch/{branchId}/opinion/{opinionId}")
    Single<String> deleteQrOpinion(@Path("branchId") int branchId, @Path("opinionId") int opinionId, @Path("organizationId") int organizationId);
}
