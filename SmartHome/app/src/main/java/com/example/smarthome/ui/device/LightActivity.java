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
import com.example.smarthome.data.model.request.LightAutoRequest;
import com.example.smarthome.data.model.request.LightControlRequest;
import com.example.smarthome.data.model.response.DeviceResponse;
import com.example.smarthome.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LightActivity extends AppCompatActivity {

    private Switch switchLight1, switchLight2, switchLight3, switchLight4, switchAutoLight;
    private ProgressBar progressBarLight;
    private ApiService apiService;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_den); // layout mới

        // Ánh xạ các view
        switchLight1 = findViewById(R.id.switchLight1);
        switchLight2 = findViewById(R.id.switchLight2);
        switchLight3 = findViewById(R.id.switchLight3);
        switchLight4 = findViewById(R.id.switchLight4);
        switchAutoLight = findViewById(R.id.switchLight_auto);
        imgBack = findViewById(R.id.img_back_home);
        progressBarLight = new ProgressBar(this); // hoặc thêm progressBar vào layout

        apiService = ApiClient.getInstance().getApiService();

        // Gắn sự kiện Switch
        switchLight1.setOnCheckedChangeListener((buttonView, isChecked) -> controlLight(1, switchLight1));
        switchLight2.setOnCheckedChangeListener((buttonView, isChecked) -> controlLight(2, switchLight2));
        switchLight3.setOnCheckedChangeListener((buttonView, isChecked) -> controlLight(3, switchLight3));
        switchLight4.setOnCheckedChangeListener((buttonView, isChecked) -> controlLight(4, switchLight4));
        switchAutoLight.setOnCheckedChangeListener((buttonView, isChecked) -> controlAutoMode(switchAutoLight));

        // Quay lại màn chính
        imgBack.setOnClickListener(view -> {
            startActivity(new Intent(LightActivity.this, MainActivity.class));
            finish();
        });
    }

    private void controlLight(int lightNumber, Switch switchView) {
        final boolean desiredStatus = switchView.isChecked();

        // Gỡ listener tạm thời và đưa về OFF
        switchView.setOnCheckedChangeListener(null);
        switchView.setChecked(false);
        switchView.setEnabled(false);
        progressBarLight.setVisibility(View.VISIBLE);

        Call<DeviceResponse> call = apiService.controlLightOne(new LightControlRequest(desiredStatus));
        if (lightNumber == 2) call = apiService.controlLightTwo(new LightControlRequest(desiredStatus));
        if (lightNumber == 3) call = apiService.controlLightThree(new LightControlRequest(desiredStatus));
        if (lightNumber == 4) call = apiService.controlLightFour(new LightControlRequest(desiredStatus));

        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                progressBarLight.setVisibility(View.GONE);
                switchView.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    switchView.setChecked(desiredStatus); // chỉ bật lại nếu thành công
                    String action = desiredStatus ? "Bật" : "Tắt";
                    Toast.makeText(LightActivity.this, action + " đèn thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LightActivity.this, "Lỗi API! Không bật được đèn", Toast.LENGTH_SHORT).show();
                }

                // Gắn lại listener
                resetSwitchListener(lightNumber, switchView);
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                progressBarLight.setVisibility(View.GONE);
                switchView.setEnabled(true);
                Toast.makeText(LightActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                switchView.setChecked(false); // giữ trạng thái OFF
                resetSwitchListener(lightNumber, switchView);
            }
        });
    }

    private void controlAutoMode(Switch switchView) {
        final boolean desiredStatus = switchView.isChecked();

        switchView.setOnCheckedChangeListener(null);
        switchView.setChecked(false);
        switchView.setEnabled(false);
        progressBarLight.setVisibility(View.VISIBLE);

        apiService.setAutoLight(new LightAutoRequest(desiredStatus)).enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                progressBarLight.setVisibility(View.GONE);
                switchView.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    switchView.setChecked(desiredStatus); // chỉ ON nếu thành công
                    String action = desiredStatus ? "Bật" : "Tắt";
                    Toast.makeText(LightActivity.this, action + " chế độ auto thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LightActivity.this, "Lỗi API khi bật Auto", Toast.LENGTH_SHORT).show();
                }

                switchView.setOnCheckedChangeListener((buttonView, isChecked) -> controlAutoMode(switchView));
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                progressBarLight.setVisibility(View.GONE);
                switchView.setEnabled(true);
                Toast.makeText(LightActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                switchView.setChecked(false); // giữ OFF nếu lỗi
                switchView.setOnCheckedChangeListener((buttonView, isChecked) -> controlAutoMode(switchView));
            }
        });
    }

    private void resetSwitchListener(int lightNumber, Switch switchView) {
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> controlLight(lightNumber, switchView));
    }
}
