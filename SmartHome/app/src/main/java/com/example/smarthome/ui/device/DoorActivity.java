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
import com.example.smarthome.data.model.request.DoorAutoRequest;
import com.example.smarthome.data.model.request.DoorControlRequest;
import com.example.smarthome.data.model.response.DeviceResponse;
import com.example.smarthome.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorActivity extends AppCompatActivity {

    private Switch switchDoor, switchAutoDoor;
    private ImageView imgBack;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door); // Giao diện mới bạn gửi trước đó

        // Ánh xạ các view từ XML mới
        switchDoor = findViewById(R.id.switchDoor_on_off);
        switchAutoDoor = findViewById(R.id.switchDoor_auto);
        imgBack = findViewById(R.id.img_back_home_fan);
        progressBar = new ProgressBar(this); // Hoặc bạn có thể thêm progressBar vào XML

        apiService = ApiClient.getInstance().getApiService();

        // Sự kiện bật/tắt cửa
        switchDoor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchDoor.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            controlDoor(isChecked);
        });

        // Sự kiện bật/tắt auto
        switchAutoDoor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchAutoDoor.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            controlAutoDoor(isChecked);
        });

        // Sự kiện quay về màn hình chính
        imgBack.setOnClickListener(v -> {
            startActivity(new Intent(DoorActivity.this, MainActivity.class));
            finish();
        });
    }

    private void controlDoor(boolean open) {
        Call<DeviceResponse> call = apiService.controlDoor(new DoorControlRequest(open));
        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                progressBar.setVisibility(View.GONE);
                switchDoor.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DoorActivity.this, open ? "Mở cửa thành công" : "Đóng cửa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DoorActivity.this, "Lỗi khi điều khiển cửa!", Toast.LENGTH_SHORT).show();
                    switchDoor.setChecked(!open); // revert trạng thái
                }
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                switchDoor.setEnabled(true);
                switchDoor.setChecked(!open);
                Toast.makeText(DoorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void controlAutoDoor(boolean autoMode) {
        Call<DeviceResponse> call = apiService.setAutoDoor(new DoorAutoRequest(autoMode));
        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                progressBar.setVisibility(View.GONE);
                switchAutoDoor.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DoorActivity.this, autoMode ? "Bật chế độ Auto" : "Tắt chế độ Auto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DoorActivity.this, "Lỗi khi điều khiển chế độ Auto!", Toast.LENGTH_SHORT).show();
                    switchAutoDoor.setChecked(!autoMode);
                }
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                switchAutoDoor.setEnabled(true);
                switchAutoDoor.setChecked(!autoMode);
                Toast.makeText(DoorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
