package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.response.BranchesResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface HomeApiService {
    @GET("branch/getBranches")
    Single<List<BranchesResponse>> getBranches();
}
