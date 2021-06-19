package com.pinus.alexdev.avis.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pinus.alexdev.avis.dto.response.ReviewStats;
import com.pinus.alexdev.avis.enums.MessageType;
import com.pinus.alexdev.avis.model.ChatMessage;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class BageCountSubscriber {
    private static final String TAG = BageCountSubscriber.class.getSimpleName();
    private  CompositeDisposable disposable = new CompositeDisposable();
    private final BehaviorSubject<ReviewStats> changeObservable = BehaviorSubject.create();
    private ReviewStats reviewStats = new ReviewStats();


    public ReviewStats getReviewStats() {
        return reviewStats;
    }

    public void setReviewStats(ReviewStats reviewStats) {
        this.reviewStats = reviewStats;
        changeObservable.onNext(this.reviewStats);
        Log.v(TAG, "New auth response2: "+  this.reviewStats.getNewAuthReviewCount() );
    }

    public Observable<ReviewStats> getModelChanges() {
        return changeObservable;
    }

    public void setBadge(String adminUsername, Context mContext) {
        StompClient mStompClient;
        String TAG = "BageCountSubscriber";
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://qr-ticket-stage.eu-west-3.elasticbeanstalk.com/ws/websocket");

        mStompClient.withClientHeartbeat(25000).withServerHeartbeat(1000);

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.v(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            Log.v(TAG, "Stomp connection error");
                            break;
                        case CLOSED:
                            Log.v(TAG, "Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.v(TAG,"Stomp failed server heartbeat");
                            break;
                    }
                });

        disposable.add(dispLifecycle);

        // Receive greetings
        Log.v(TAG, "/channel/"+adminUsername);
        Disposable dispTopic = mStompClient.topic("/channel/"+adminUsername)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    ChatMessage object = new Gson().fromJson(topicMessage.getPayload(), ChatMessage.class);
                    if(object.getMessageType() == MessageType.STATS)
                            changeObservable.onNext(object.getReviewStats());
                }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

        disposable.add(dispTopic);

        mStompClient.connect();
    }

    private void resetSubscriptions() {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = new CompositeDisposable();
    }

}
