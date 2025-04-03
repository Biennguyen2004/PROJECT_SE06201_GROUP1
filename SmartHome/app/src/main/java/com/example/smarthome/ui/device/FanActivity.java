package com.example.smarthome.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthome.R;
import com.example.smarthome.data.api.ApiClient;
import com.example.smarthome.data.api.ApiService;
import com.example.smarthome.data.model.request.FanAutoRequest;
import com.example.smarthome.data.model.request.FanControlRequest;
import com.example.smarthome.data.model.response.DeviceResponse;
import com.example.smarthome.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FanActivity extends AppCompatActivity {

    private Switch switchFanOnOff, switchFanAuto;
    private ProgressBar progressBarFan;
    private ImageView imgBackFan;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan); // layout mới đã chỉnh sửa

        // Ánh xạ view theo giao diện mới
        switchFanOnOff = findViewById(R.id.switchLight_on_off);
        switchFanAuto = findViewById(R.id.switchLight_auto);
        imgBackFan = findViewById(R.id.img_back_home_fan);
        progressBarFan = new ProgressBar(this); // hoặc findViewById nếu bạn đã thêm vào layout

        apiService = ApiClient.getInstance().getApiService();

        // Gắn sự kiện bật/tắt quạt
        switchFanOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> controlFanSwitch(switchFanOnOff));

        // Gắn sự kiện auto quạt
        switchFanAuto.setOnCheckedChangeListener((buttonView, isChecked) -> controlAutoFanSwitch(switchFanAuto));

        // Quay lại màn chính
        imgBackFan.setOnClickListener(view -> {
            startActivity(new Intent(FanActivity.this, MainActivity.class));
            finish();
        });
    }

    private void controlFanSwitch(Switch fanSwitch) {
        final boolean desiredStatus = fanSwitch.isChecked();

        // Đưa về OFF tạm thời
        fanSwitch.setOnCheckedChangeListener(null);
        fanSwitch.setChecked(false);
        fanSwitch.setEnabled(false);
        progressBarFan.setVisibility(View.VISIBLE);

        Call<DeviceResponse> call = apiService.controlFan(new FanControlRequest(desiredStatus));
        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                progressBarFan.setVisibility(View.GONE);
                fanSwitch.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    fanSwitch.setChecked(desiredStatus);
                    String msg = desiredStatus ? "Bật quạt thành công" : "Tắt quạt thành công";
                    Toast.makeText(FanActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FanActivity.this, "Lỗi API điều khiển quạt!", Toast.LENGTH_SHORT).show();
                }

                fanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> controlFanSwitch(fanSwitch));
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                progressBarFan.setVisibility(View.GONE);
                fanSwitch.setEnabled(true);
                fanSwitch.setChecked(false);
                Toast.makeText(FanActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                fanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> controlFanSwitch(fanSwitch));
            }
        });
    }

    private void controlAutoFanSwitch(Switch autoSwitch) {
        final boolean desiredStatus = autoSwitch.isChecked();

        autoSwitch.setOnCheckedChangeListener(null);
        autoSwitch.setChecked(false);
        autoSwitch.setEnabled(false);
        progressBarFan.setVisibility(View.VISIBLE);

        Call<DeviceResponse> call = apiService.setAutoFan(new FanAutoRequest(desiredStatus));
        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                progressBarFan.setVisibility(View.GONE);
                autoSwitch.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    autoSwitch.setChecked(desiredStatus);
                    String msg = desiredStatus ? "Bật chế độ Auto" : "Tắt chế độ Auto";
                    Toast.makeText(FanActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FanActivity.this, "Lỗi API chế độ Auto!", Toast.LENGTH_SHORT).show();
                }

                autoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> controlAutoFanSwitch(autoSwitch));
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                progressBarFan.setVisibility(View.GONE);
                autoSwitch.setEnabled(true);
                autoSwitch.setChecked(false);
                Toast.makeText(FanActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                autoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> controlAutoFanSwitch(autoSwitch));
            }
        });
    }
}
