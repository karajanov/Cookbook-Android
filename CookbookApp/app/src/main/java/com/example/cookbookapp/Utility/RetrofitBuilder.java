package com.example.cookbookapp.Utility;

import android.text.TextUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private Retrofit.Builder builder;

    public RetrofitBuilder() {
        builder = new Retrofit.Builder();
    }

    public Retrofit getBuilder(String paramBaseUrl) {
        if(paramBaseUrl == null || TextUtils.isEmpty(paramBaseUrl)) {
            return null;
        }
        return builder.baseUrl(paramBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
