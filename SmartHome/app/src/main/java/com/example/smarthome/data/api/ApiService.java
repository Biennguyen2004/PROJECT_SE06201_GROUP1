package com.example.smarthome.data.api;

import com.example.smarthome.data.model.Request.LoginRequest;
import com.example.smarthome.data.model.Request.SignupRequest;
import com.example.smarthome.data.model.response.LoginResponse;
import com.example.smarthome.data.model.response.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/register")
    Call<SignupResponse> register(@Body SignupRequest signupRequest);

    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
