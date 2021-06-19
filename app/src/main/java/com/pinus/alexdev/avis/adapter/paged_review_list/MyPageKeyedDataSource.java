package com.pinus.alexdev.avis.adapter.paged_review_list;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.ReviewsApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;

public class MyPageKeyedDataSource extends PageKeyedDataSource<Integer, ReviewResponse> {
    private Context context;
    private String orderBy;
    private String sortBy;

    private ReviewsApiService reviewsApiService;
    private SaveLoadData saveLoadData;

    private final int PAGE_SIZE = 10;
    private int page = 1;

    private static final String TAG = MyPageKeyedDataSource.class.getSimpleName();

    public MyPageKeyedDataSource(Context context, String orderBy, String sortBy) {
        this.context = context;
        this.orderBy = orderBy;
        this.sortBy = sortBy;

        reviewsApiService = ApiClient.getClient(context).create(ReviewsApiService.class);
        saveLoadData = new SaveLoadData(context);
    }

    interface CallBackOnResult {
        void setOnResult(ArrayList<ReviewResponse> reviews);
    }

    private void loadData(CallBackOnResult callback, String orderBy, int page, String sortBy) {
        if (orderBy.isEmpty() && sortBy.isEmpty())
            reviewsApiService
                    .getReviewsWithoutSorting(saveLoadData.loadInt(ORGANIZATION_ID_KEY), page, PAGE_SIZE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::setOnResult,
                            e -> Log.e(TAG, "onError:" + e.getMessage()));
        if (!orderBy.isEmpty() && sortBy.isEmpty())
            reviewsApiService
                    .getReviewsByOrder(saveLoadData.loadInt(ORGANIZATION_ID_KEY), orderBy, page, PAGE_SIZE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::setOnResult,
                            e -> Log.e(TAG, "onError:" + e.getMessage()));
        if (!sortBy.isEmpty() && orderBy.isEmpty())
            reviewsApiService
                    .getReviewsBySorting(saveLoadData.loadInt(ORGANIZATION_ID_KEY), page, PAGE_SIZE, sortBy)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::setOnResult,
                            e -> Log.e(TAG, "onError:" + e.getMessage()));
        if (!orderBy.isEmpty() && !sortBy.isEmpty())
            reviewsApiService
                    .getReviews(saveLoadData.loadInt(ORGANIZATION_ID_KEY), orderBy, page, PAGE_SIZE, sortBy)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::setOnResult,
                            e -> Log.e(TAG, "onError:" + e.getMessage()));
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, ReviewResponse> callback) {
        loadData(reviews -> callback.onResult(reviews, null, page + 1), orderBy, page, sortBy);
        Log.v("PageTag", "loadInitial params.key: " + page);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ReviewResponse> callback) {
        loadData(reviews -> callback.onResult(reviews, params.key + 1), orderBy, params.key, sortBy);
        Log.v("PageTag", "loadBefore params.key: " + params.key);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ReviewResponse> callback) {
        loadData(reviews -> callback.onResult(reviews, params.key + 1), orderBy, params.key, sortBy);
        Log.v("PageTag", "loadAfter params.key: " + params.key);
    }
}
