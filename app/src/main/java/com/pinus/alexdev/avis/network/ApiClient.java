package com.pinus.alexdev.avis.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pinus.alexdev.avis.BuildConfig;
import com.pinus.alexdev.avis.utils.PrefUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;
    private static String BASE_URL = "https://qr-ticket-stage.eu-west-3.elasticbeanstalk.com/api/v1/";
    private static String TAG = "ApiClient";

    public static Retrofit getClient(Context context) {
        if (okHttpClient == null)
            initOkHttp(context);

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp(final Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        //.addInterceptor(new Interceptors.OfflineCacheInterceptor())
        //   .addNetworkInterceptor(new Interceptors.NetworkCacheInterceptor());
        //    .cache(Interceptors.provideCache());

        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(Interceptors.loggingInterceptor(TAG));

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(x -> {
            Request.Builder requestBuilder = x.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json");
            if (!TextUtils.isEmpty(PrefUtils.getApiKey(context))) {
                requestBuilder.addHeader("Authorization", PrefUtils.getApiKey(context));
            }
            return x.proceed(requestBuilder.build());
        });
        okHttpClient = httpClient.build();
    }
}
