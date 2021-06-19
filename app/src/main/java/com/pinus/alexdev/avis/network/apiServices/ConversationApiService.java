package com.pinus.alexdev.avis.network.apiServices;

import com.pinus.alexdev.avis.dto.Conversation;
import com.pinus.alexdev.avis.dto.request.ChatByNumberRequest;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.dto.response.ConversationResponse;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConversationApiService {
    @GET("/api/v1/chat/{chatId}/message/all")
    Single<ArrayList<Conversation>> getConversationHistory(@Path("chatId") String chatId);

    @POST("/api/v1/chat/{chatId}")
    Single<String> setChatViewed(@Path("chatId") String reviewId);

    @GET("/api/v1/organization/{organizationId}/chat")
    Single<ArrayList<ConversationResponse>> getOrganizationConversationsList(@Path("organizationId") int organizationId);

    @GET("/api/v1/organization/{organizationId}/bundle/chats")
    Single<ArrayList<ConversationResponse>> getConversationsListByBranchId(@Path("organizationId") int organizationId, @Query("branchIdList") int branchIdList);

    @POST("/api/v1/organization/{organizationId}/branch/{branchId}/chat")
    Single<ConversationResponse> addChatByNumber(@Path("organizationId") int organizationId, @Body ChatByNumberRequest chatByNumberRequest, @Path("branchId") int branchId);

    @POST("/api/v1/organization/review/{reviewId}/chat")
    Single<ConversationResponse> addChatByReviewId(@Path("reviewId") int reviewId);

    @POST("/api/v1/chat/chat-room/{roomId}/message")
    Single<ArrayList<Conversation>> getChatList(@Path("roomId") int roomId, @Query("order") String order, @Query("page") int page, @Query("size") int size, @Query("sortBy") String sortBy);

    @GET("/api/v1/organization/{organizationId}/branch")
    Single<ArrayList<BranchesResponse>> getBranchList(@Path("organizationId") int organizationId);
}
