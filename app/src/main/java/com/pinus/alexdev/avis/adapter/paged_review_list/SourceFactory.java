package com.pinus.alexdev.avis.adapter.paged_review_list;

import android.content.Context;

import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.pinus.alexdev.avis.dto.response.review_response.ReviewResponse;
import com.pinus.alexdev.avis.utils.SingleLiveEvent;

public class SourceFactory extends DataSource.Factory {
    private Context context;
    private String orderBy;
    private String sortBy;

    private MyPageKeyedDataSource pageKeyedDataSource;
    private SingleLiveEvent<PageKeyedDataSource<Integer, ReviewResponse>> liveDataSource;

    public SourceFactory(Context context, String orderBy, String sortBy) {
        this.context = context;
        this.orderBy = orderBy;
        this.sortBy = sortBy;
    }

    @Override
    public DataSource create() {
        pageKeyedDataSource = new MyPageKeyedDataSource(context, orderBy, sortBy);
        liveDataSource = new SingleLiveEvent<>();
        liveDataSource.postValue(pageKeyedDataSource);
        return pageKeyedDataSource;
    }

    public SingleLiveEvent<PageKeyedDataSource<Integer, ReviewResponse>> getLiveDataSource() {
        return liveDataSource;
    }

    //Метод для обновления данных в списке
    public void setNewValuesToDataSource(Context context, String orderBy, String sortBy) {
        this.context = context;
        this.orderBy = orderBy;
        this.sortBy = sortBy;
        pageKeyedDataSource.invalidate();
    }
}
