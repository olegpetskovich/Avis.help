package com.pinus.alexdev.avis.network;

import android.util.Log;

import com.pinus.alexdev.avis.utils.App;
import com.pinus.alexdev.avis.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class Interceptors {
    private static final int MAX_AGE = 5;
    private static final long MAX_STALE = 10;

    public static class OfflineCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (!Utils.isNetworkAvailable(App.getContext())) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(1, TimeUnit.DAYS)
                        .build();
                request = request.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
            return chain.proceed(request);
        }
    }


    public static class NetworkCacheInterceptor implements Interceptor {

        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            String cacheHeaderValue = Utils.isNetworkAvailable(App.getContext())
                    ? "public, max-age=" + MAX_AGE
                    : "public, only-if-cached, max-stale=" + MAX_STALE;
            Request request = originalRequest.newBuilder().build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheHeaderValue)
                    .build();
        }
    }

    public static HttpLoggingInterceptor loggingInterceptor(final String tag) {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(tag, message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.HEADERS);
    }

    public static Cache provideCache() {
        Cache cache = null;
        try {
            File dir = App.getContext().getCacheDir();
            cache = new Cache(new File(dir, "http-cache"), 10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }
}