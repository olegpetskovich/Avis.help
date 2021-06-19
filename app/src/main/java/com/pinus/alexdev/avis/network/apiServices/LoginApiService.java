package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.request.LoginRequest;
import com.pinus.alexdev.avis.dto.response.login_response.LoginResponse;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginApiService {
    @POST("/api/v1/auth/login")
    Single<LoginResponse> signIn(@Body LoginRequest loginRequest);

    @POST("/api/v1/user/profile/reset-password")
    Single<String> resetPassword(@Header("Accept-Language") String accept_language, @Query("email") String email);
}