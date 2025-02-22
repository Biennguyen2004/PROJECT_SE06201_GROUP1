package com.example.smarthome.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://b8a9-1-55-211-160.ngrok-free.app/api/v1/";

    private static ApiClient mInstance;
    private Retrofit retrofit;

    private ApiClient() {
        // Gán giá trị cho biến retrofit của class
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (mInstance == null) {
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
