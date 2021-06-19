package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.dto.response.StatusResponse;
import com.pinus.alexdev.avis.enums.ReviewStatus;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewsApiService {
    @GET("/api/v1/organization/review/{reviewId}")
    Single<ReviewResponse> getReviewInfo(@Path("reviewId") int reviewId);

    @GET("/api/v1/organization/{organizationId}/review")
    Single<ArrayList<ReviewResponse>> getReviews(@Path("organizationId") int organizationId, @Query("order") String order, @Query("page") int page, @Query("size") int size, @Query("sortBy") String sortBy);

    @GET("/api/v1/organization/{organizationId}/review")
    Single<ArrayList<ReviewResponse>> getReviewsBySorting(@Path("organizationId") int organizationId, @Query("page") int page, @Query("size") int size, @Query("sortBy") String sortBy);

    @GET("/api/v1/organization/{organizationId}/review")
    Single<ArrayList<ReviewResponse>> getReviewsByOrder(@Path("organizationId") int organizationId, @Query("order") String order, @Query("page") int page, @Query("size") int size);

    @GET("/api/v1/organization/{organizationId}/review")
    Single<ArrayList<ReviewResponse>> getReviewsWithoutSorting(@Path("organizationId") int organizationId, @Query("page") int page, @Query("size") int size);

    @GET("review/getReviewsWithConversation")
    Single<ArrayList<ReviewResponse>> getReviewsWithConversation();

    @POST("review/addComment/{reviewId}")
    Single<StatusResponse> addComment(@Path("reviewId") long reviewId, @Query("comment") String comment);

    @PATCH("/api/v1/organization/review/{reviewId}/set-viewed")
    Single<ReviewResponse> setViewed(@Path("reviewId") long reviewId, @Query("state") boolean state);
}
