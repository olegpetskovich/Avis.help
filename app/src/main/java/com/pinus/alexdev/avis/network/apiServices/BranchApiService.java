package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.request.BranchRequest;
import com.pinus.alexdev.avis.dto.request.OrganizationRequest;
import com.pinus.alexdev.avis.dto.response.branch_statistic_response.BranchStatisticResponse;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.dto.response.OrganizationResponse;
import com.pinus.alexdev.avis.dto.response.ReviewStats;
import com.pinus.alexdev.avis.dto.response.StatsResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.QrManagerListResponse;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BranchApiService {
    @GET("/api/v1/organization/{organizationId}/branch/{branchId}/bundle/qr-type")
    Single<QrManagerListResponse> getBranchQrsList(@Path("branchId") long branchId, @Path("organizationId") int organizationId);

    @GET("/api/v1/organization/{organizationId}/branch")
    Single<ArrayList<BranchesResponse>> getBranchList(@Path("organizationId") int organizationId);

    @DELETE("/api/v1/organization/{organizationId}/branch/{branchId}")
    Observable<String> deleteBranch(@Path("branchId") int branchId, @Path("organizationId") int organizationId);

    @GET("/api/v1/organization/{organizationId}/bundle/category")
    Single<ArrayList<String>> getCategoryList(@Path("organizationId") int organizationId);

    //Запрос для получения количества новых сообщений и ревью
    @GET("/api/v1/organization/{organizationId}/chat/newMessages")
    Single<Integer> getStatistic(@Path("organizationId") int organizationId);

    @POST("/api/v1/organization/{organizationId}/branch")
    Single<BranchesResponse> addBranch(@Body BranchRequest request, @Path("organizationId") int organizationId);

    @PATCH("/api/v1/organization/{organizationId}/branch/{branchId}")
    Single<BranchesResponse> updateBranchInfo(@Body BranchRequest request, @Path("branchId") int branchId, @Path("organizationId") int organizationId);

    @Multipart
    @POST("/api/v1/organization/{organizationId}/branch/{branchId}/logo")
    Single<String> updateBranchPhoto(@Path("branchId") int branchId, @Part("file") RequestBody description, @Part MultipartBody.Part photo, @Path("organizationId") int organizationId);

    @GET("/api/v1/organization/{organizationId}/branch/{branchId}/statistics")
    Single<BranchStatisticResponse> getBranchStatistic(@Path("branchId") long branchId,
                                                       @Path("organizationId") int organizationId,
                                                       @Query("category") String category,
                                                       @Query("from") String from,
                                                       @Query("range") String range,
                                                       @Query("to") String to);

    @GET("/api/v1/organization/{organizationId}/branch/{branchId}/opinion/{opinionId}/statistics")
    Single<BranchStatisticResponse> getBranchOpinionStatistic(@Path("branchId") long branchId,
                                                              @Path("organizationId") int organizationId,
                                                              @Path("opinionId") long opinionId,
                                                              @Query("from") String from,
                                                              @Query("range") String range,
                                                              @Query("to") String to);

    @GET("/api/v1/organization/{organizationId}/statistics")
    Single<BranchStatisticResponse> getAllOrganizationStatistic(@Path("organizationId") int organizationId,
                                                                @Query("category") String category,
                                                                @Query("from") String from,
                                                                @Query("range") String range,
                                                                @Query("to") String to);

    @POST("/api/v1/organization")
    Single<StatsResponse> addOrganization(@Body OrganizationRequest request);

    @GET("/api/v1/organization/{organizationId}")
    Single<OrganizationResponse> getOrganization(@Path("organizationId") int organizationId);

    @PATCH("/api/v1/organization/{organizationId}")
    Single<OrganizationResponse> updateOrganizationName(@Body OrganizationRequest request, @Path("organizationId") int organizationId);

    @Multipart
    @POST("/api/v1/organization/{organizationId}/logo")
    Single<String> updateOrganizationPhoto(@Part("file") RequestBody description, @Part MultipartBody.Part photo, @Path("organizationId") int organizationId);

    @DELETE("/api/v1/organization/{organizationId}")
    Observable<String> deleteOrganization(@Path("organizationId") int organizationId);
}
