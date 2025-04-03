package com.example.smarthome.data.api;

import com.example.smarthome.data.model.request.DoorAutoRequest;
import com.example.smarthome.data.model.request.DoorControlRequest;
import com.example.smarthome.data.model.request.FanAutoRequest;
import com.example.smarthome.data.model.request.FanControlRequest;
import com.example.smarthome.data.model.request.LightAutoRequest;
import com.example.smarthome.data.model.request.LightControlRequest;
import com.example.smarthome.data.model.request.LoginRequest;
import com.example.smarthome.data.model.request.SignupRequest;
import com.example.smarthome.data.model.response.DeviceResponse;
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
    Call<DeviceResponse> controlDoor(@Body DoorControlRequest request);

    // API bật/tắt chế độ Auto cửa
    @POST("door/auto")
    Call<DeviceResponse> setAutoDoor(@Body DoorAutoRequest request);

    // Điều khiển quạt (bật/tắt)
    @POST("fan/control")
    Call<DeviceResponse> controlFan(@Body FanControlRequest request);

    // Bật/tắt chế độ Auto của quạt
    @POST("fan/auto")
    Call<DeviceResponse> setAutoFan(@Body FanAutoRequest request);

    @POST("light/control/one")
    Call<DeviceResponse> controlLightOne(@Body LightControlRequest request);

    @POST("light/control/two")
    Call<DeviceResponse> controlLightTwo(@Body LightControlRequest request);

    @POST("light/control/three")
    Call<DeviceResponse> controlLightThree(@Body LightControlRequest request);

    @POST("light/control/four")
    Call<DeviceResponse> controlLightFour(@Body LightControlRequest request);

    @POST("light/auto")
    Call<DeviceResponse> setAutoLight(@Body LightAutoRequest request);
}
