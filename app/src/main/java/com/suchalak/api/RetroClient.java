package com.suchalak.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static final String ROOT_URL = "http://cegepclm.com/";
    public static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
