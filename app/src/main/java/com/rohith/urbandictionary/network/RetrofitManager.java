package com.rohith.urbandictionary.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final String BASE_URL = "https://mashape-community-urban-dictionary.p.rapidapi.com";
    private static RetrofitManager sInstance;

    private final UrbanDictionaryApi mUrbanDictionaryApi;

    private RetrofitManager() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        mUrbanDictionaryApi = retrofit.create(UrbanDictionaryApi.class);
    }

    public static synchronized RetrofitManager getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitManager();
        }
        return sInstance;
    }

    public UrbanDictionaryApi getClient() {
        return mUrbanDictionaryApi;
    }
}
