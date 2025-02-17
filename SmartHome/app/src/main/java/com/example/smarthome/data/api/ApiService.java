package com.example.smarthome.data.api;

import com.example.smarthome.data.model.Request.LoginRequest;
import com.example.smarthome.data.model.response.LoginResponse;
import com.example.smarthome.data.model.response.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("user/register")
    Call<SignupResponse> register(
            @Field("username") String username,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("password") String password,
            @Field("repassword") String repassword
    );


    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);



}
