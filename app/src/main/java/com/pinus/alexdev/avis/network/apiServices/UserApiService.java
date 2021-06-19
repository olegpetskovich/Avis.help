package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.request.ChangePasswordRequest;
import com.pinus.alexdev.avis.dto.request.UserRequest;
import com.pinus.alexdev.avis.dto.response.user_summary_response.UserSummaryResponse;
import com.pinus.alexdev.avis.dto.response.user_updated_response.UserUpdatedResponse;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApiService {
    @GET("/api/v1/user/me")
    Single<UserSummaryResponse> getUser();

    @GET("/api/v1/user/{userId}")
    Single<UserSummaryResponse> getUserById(@Path("userId") int userId);

    @PATCH("/api/v1/user/{userId}")
    Single<UserUpdatedResponse> updateUser(@Body UserRequest userRequest, @Path("userId") int userId);

    @POST("/api/v1/user/{userId}/change-password")
    Single<String> updatePassword(@Body ChangePasswordRequest changePasswordRequest, @Path("userId") int userId);

    @Multipart
    @POST("/api/v1/user/{userId}/logo")
    Single<String> updateAvatar(@Part("file") RequestBody description, @Part MultipartBody.Part photo, @Path("userId") int userId);
}
