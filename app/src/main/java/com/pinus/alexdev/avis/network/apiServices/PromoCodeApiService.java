package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.request.PromoCodeRequest;
import com.pinus.alexdev.avis.dto.request.PromoScanRequest;
import com.pinus.alexdev.avis.dto.request.PromoSmsRequest;
import com.pinus.alexdev.avis.dto.response.promo_code_response.ProceedPromoResponse;
import com.pinus.alexdev.avis.dto.response.promo_code_response.PromoCodeResponse;
import com.pinus.alexdev.avis.dto.response.ScarQrResponse;
import com.pinus.alexdev.avis.dto.response.StatusResponse;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PromoCodeApiService {
    @POST("/api/v1/organization/{organizationId}/promo")
    Single<PromoCodeResponse> addPromoCode(@Path("organizationId") int organizationId, @Body PromoCodeRequest loginRequest);

    @POST("/api/v1/organization/{organizationId}/promo/{promoId}/send-via-sms")
    Single<String> sendPromoCode(@Header("Accept-Language") String accept_language, @Body PromoSmsRequest smsRequest, @Path("organizationId") int organizationId, @Path("promoId") long promoId);

    @POST("/api/v1/organization/{organizationId}/promo/proceed/{humanId}")
    Single<ProceedPromoResponse> proceedPromoCode(@Path("humanId") String humanId, @Path("organizationId") int organizationId);

    @GET("/api/v1/organization/{organizationId}/promo")
    Single<ArrayList<PromoCodeResponse>> getPromoCodes(@Path("organizationId") int organizationId);

    @DELETE("/api/v1/organization/{organizationId}/promo/{promoId}")
    Observable<String> deletePromo(@Path("organizationId") int organizationId, @Path("promoId")int promoId);

    @POST("/api/v1/promo/scan")
    Single<String> scanQrCode(@Body PromoScanRequest scanRequest);

    @POST("promocode/validate")
    Single<ScarQrResponse> validateQrCode(@Query("promoCode") String promoCode);
}
