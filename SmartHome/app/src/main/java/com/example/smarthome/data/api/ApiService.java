package com.example.smarthome.data.api;

import com.example.smarthome.data.model.request.DoorAutoRequest;
import com.example.smarthome.data.model.request.DoorControlRequest;
import com.example.smarthome.data.model.request.LoginRequest;
import com.example.smarthome.data.model.request.SignupRequest;
import com.example.smarthome.data.model.response.DoorResponse;
import com.example.smarthome.data.model.response.LoginResponse;
import com.example.smarthome.data.model.response.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("v1/user/register")
    Call<SignupResponse> register(@Body SignupRequest signupRequest);

    @POST("v1/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // API điều khiển cửa (Mở/Đóng)
    @POST("door/control")
    Call<DoorResponse> controlDoor(@Body DoorControlRequest request);

    // API bật/tắt chế độ Auto cửa
    @POST("door/auto")
    Call<DoorResponse> setAutoDoor(@Body DoorAutoRequest request);
}
