package com.pinus.alexdev.avis.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.pinus.alexdev.avis.callback.ICallback;
import com.pinus.alexdev.avis.dto.response.user_summary_response.UserSummaryResponse;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.UserApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static com.pinus.alexdev.avis.view.LoginActivity.USER_ID_KEY;

public class UserDataPref {
    private static final String TAG = UserDataPref.class.getSimpleName();
    BehaviorSubject<UserSummaryResponse> behaviorSubject = BehaviorSubject.create();
    UserSummaryResponse userSummaryResponse;

    //Класс для удобной работы с SharedPreferences
    private static SaveLoadData saveLoadData;

    public UserDataPref() {

    }

    public static void userRequest(Context context, ICallback ic) {
        saveLoadData = new SaveLoadData(context);

        UserApiService userApiService = ApiClient.getClient(context).create(UserApiService.class);
        new CompositeDisposable().add(userApiService.getUserById(saveLoadData.loadInt(USER_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        t -> {
                            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
                            editor.putString("meUserObject", new Gson().toJson(t));
                            editor.commit();
                            ic.callback(t);
                            Log.v(TAG, "USERID: " + t.getFirstName());
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));
    }

    public static void userRequest(Context context) {
        saveLoadData = new SaveLoadData(context);
        UserApiService userApiService = ApiClient.getClient(context).create(UserApiService.class);
        new CompositeDisposable().add(userApiService.getUserById(saveLoadData.loadInt(USER_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        t -> {
                            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
                            editor.putString("meUserObject", new Gson().toJson(t));
                            editor.commit();
                            Log.v(TAG, "USERID: " + t.getFirstName());
                        },
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("USEROBJECT", Context.MODE_PRIVATE);
    }

    public static UserSummaryResponse getUserSummaryInfo(Context context) {
        return UserSummaryResponse.create(getSharedPreferences(context).getString("meUserObject", null));
    }
}
