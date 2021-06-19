package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.response.CompleteCtaResponse;
import com.pinus.alexdev.avis.dto.response.NewCtaResponse;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CTAService {
    @GET("cta/getNewCta")
    Single<ArrayList<NewCtaResponse>> getNewCtaList();

    @PUT("cta/completeCta")
    Single<CompleteCtaResponse> completeCta(@Query("scannedCtaId") long scannedCtaId);
}
