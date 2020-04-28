package com.example.cookbookapp.Utility;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private static Retrofit builder = null;

    public static Retrofit getBuilder(String paramBaseUrl) {

        if(paramBaseUrl == null || TextUtils.isEmpty(paramBaseUrl))
            return null;

        if(builder == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

            builder = new Retrofit.Builder()
                    .baseUrl(paramBaseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return builder;
    }
}
