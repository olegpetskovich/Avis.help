package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.request.TokenRequest;
import com.pinus.alexdev.avis.dto.response.DeviceResponse;
import com.pinus.alexdev.avis.dto.response.StatusResponse;
import com.pinus.alexdev.avis.dto.response.StatusResponseBool;
import com.pinus.alexdev.avis.dto.response.TokenResponse;
import com.pinus.alexdev.avis.enums.NotificationOs;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationApiService {
    @POST("notification/registerDevice")
    Single<StatusResponse> registerDevice(@Query("notificationOs") NotificationOs notificationOs, @Query("token") String token);

    @POST("notification/setLogout")
    Single<StatusResponse> setLogout(@Query("token") String token);

    @POST("notification/setLogin")
    Single<StatusResponse> setLogin(@Query("notificationOs") NotificationOs notificationOs, @Query("token") String token);

    @GET("notification/getStatus")
    Single<StatusResponseBool> getStatus(@Query("token") String token);

    @GET("/api/v1/user/{userId}/notification-token")
    Single<StatusResponse> getToken(@Query("token") String token);

    @POST("/api/v1/user/{userId}/notification-token")
    Single<DeviceResponse> addToken(@Body TokenRequest tokenDto, @Path("userId") int userId);

    @PATCH("/api/v1/user/{userId}/notification-token/{tokenId}")
    Single<DeviceResponse> updateToken(@Path("tokenId") long tokenId, @Body TokenRequest tokenDto, @Path("userId") int userId);
}
