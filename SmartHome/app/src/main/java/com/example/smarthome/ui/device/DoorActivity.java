package com.example.smarthome.ui.device;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;

import com.example.smarthome.R;
import com.example.smarthome.data.api.ApiClient;
import com.example.smarthome.data.api.ApiService;
import com.example.smarthome.data.model.request.DoorAutoRequest;
import com.example.smarthome.data.model.request.DoorControlRequest;
import com.example.smarthome.data.model.response.DoorResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorActivity extends AppCompatActivity {

    private Button btnToggleAuto;
    private Button btnMoDongCua;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_door);

        btnToggleAuto = findViewById(R.id.button); // Nút Auto Mode

        btnMoDongCua = findViewById(R.id.button2); // Nút mở và đóng cửa

        progressBar = findViewById(R.id.progressBar);

        apiService = ApiClient.getInstance().getApiService();

        btnToggleAuto.setOnClickListener(view -> toggleAutoMode());
        btnMoDongCua.setOnClickListener(view -> toggleMoDongCua());
    }

    private void toggleAutoMode() {

        boolean isAutoOn = btnToggleAuto.getText().toString().equals("Bật Auto");

        sendAutoModeRequest(isAutoOn);
    }

    private void toggleMoDongCua() {
        boolean isMoDongCua = btnMoDongCua.getText().toString().equals("Mở Cửa");

        sendRequestAPI(isMoDongCua);
    }

    private void sendRequestAPI(boolean isMoDongCua) {

        btnToggleAuto.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar

        Call<DoorResponse> call = apiService.controlDoor(new DoorControlRequest(isMoDongCua));
        call.enqueue(new Callback<DoorResponse>() {
            @Override
            public void onResponse(Call<DoorResponse> call, Response<DoorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    btnToggleAuto.setEnabled(true);
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi API trả về

                    Toast.makeText(DoorActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                    updateDongMoCuaButton(isMoDongCua);
                } else {
                    try {
                        // Lấy dữ liệu từ response lỗi
                        String errorBody = response.errorBody().string();
                        Toast.makeText(DoorActivity.this, "Lỗi API: " + errorBody, Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", "Response: " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(DoorActivity.this, "Lỗi chế độ Auto!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DoorResponse> call, Throwable t) {
                btnToggleAuto.setEnabled(true);
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi gặp lỗi

                if (t instanceof java.net.UnknownHostException) {
                    Toast.makeText(DoorActivity.this, "Không có kết nối mạng!", Toast.LENGTH_LONG).show();
                } else if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(DoorActivity.this, "Kết nối đến server bị timeout!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DoorActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.e("API_ERROR", "Lỗi khi gọi API", t);
            }
        });

    }

    private void updateDongMoCuaButton(boolean isMoDongCua) {
        if (isMoDongCua) {
            btnMoDongCua.setText("Đóng Cửa");
            btnMoDongCua.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOff));
        } else {
            btnMoDongCua.setText("Mở Cửa");
            btnMoDongCua.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOn));
        }
    }



    private void sendAutoModeRequest(boolean status) {

        btnToggleAuto.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar

        Call<DoorResponse> call = apiService.setAutoDoor(new DoorAutoRequest(status));
        call.enqueue(new Callback<DoorResponse>() {
            @Override
            public void onResponse(Call<DoorResponse> call, Response<DoorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    btnToggleAuto.setEnabled(true);
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi API trả về

                    Toast.makeText(DoorActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                    updateAutoButton(status);
                } else {
                    try {
                        // Lấy dữ liệu từ response lỗi
                        String errorBody = response.errorBody().string();
                        Toast.makeText(DoorActivity.this, "Lỗi API: " + errorBody, Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", "Response: " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(DoorActivity.this, "Lỗi chế độ Auto!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DoorResponse> call, Throwable t) {

                btnToggleAuto.setEnabled(true);
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi gặp lỗi

                if (t instanceof java.net.UnknownHostException) {
                    Toast.makeText(DoorActivity.this, "Không có kết nối mạng!", Toast.LENGTH_LONG).show();
                } else if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(DoorActivity.this, "Kết nối đến server bị timeout!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DoorActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.e("API_ERROR", "Lỗi khi gọi API", t);
            }
        });
    }

    private void updateAutoButton(boolean isAutoOn) {
        if (isAutoOn) {
            btnToggleAuto.setText("Tắt Auto");
            btnToggleAuto.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOff));
        } else {
            btnToggleAuto.setText("Bật Auto");
            btnToggleAuto.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOn));
        }
    }
}
