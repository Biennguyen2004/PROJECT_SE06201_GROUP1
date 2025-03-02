package com.example.smarthome.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthome.R;
import com.example.smarthome.network.GasWebSocketClient;
import com.example.smarthome.network.HumidityWebSocketClient;
import com.example.smarthome.network.WaterWebSocketClient;
import com.example.smarthome.ui.device.DoorActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements GasWebSocketClient.GasWebSocketListener, WaterWebSocketClient.WaterWebSocketListener, HumidityWebSocketClient.HumidityWebSocketListener {

    private View mView;

    private ImageView imgDoor;

    // gas
    private TextView tvGas;
    private GasWebSocketClient gasWebSocketClient;

    // water
    private TextView tvWater;
    private WaterWebSocketClient waterWebSocketClient;

    // độ ẩm
    private TextView tvHumidity;
    private HumidityWebSocketClient humidityWebSocketClient;

    // thời tiết
    private TextView cityNameText, temperatureText, humidityText, descriptionText, windText;
    private ImageView weatherIcon;
    private static final String API_KEY = "f2583b0aa73fb5c781f2a9d138300e73";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        // ====== Hiển thị ======
        DisplayGas();

        DisplayWater();

        DisplayHumidity();

        DisplayBreakin();

        DisplayWeather();

        // ===== Điều khiển thiet bị ====

        doorControl();

        lightControl();

        fanControl();

        return mView;
    }

    // Hien thi thoi tiet
    private void DisplayWeather() {

        cityNameText = mView.findViewById(R.id.tv_city_name);
        temperatureText = mView.findViewById(R.id.tv_temperature);
        humidityText = mView.findViewById(R.id.tv_humidity);
        windText = mView.findViewById(R.id.tv_wind);
        descriptionText = mView.findViewById(R.id.tv_des);
        weatherIcon = mView.findViewById(R.id.weatherIcon);

        FetchWeatherData("Hanoi");
    }

    private void FetchWeatherData(String cityName) {

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric";

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    updateUI(result);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUI(String result) {

        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");

                String resourceName = "ic_"+ iconCode;
                int resId = getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());
                weatherIcon.setImageResource(resId);

                cityNameText.setText(jsonObject.getString("name"));
                temperatureText.setText(String.format("%.0f °C", temperature));
                humidityText.setText(String.format("%.0f%%", humidity));
                windText.setText(String.format("%.0f km/h", windSpeed));
                descriptionText.setText(description);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // điều khiển quạt
    private void fanControl() {



    }

    // điều khiển đèn
    private void lightControl() {
    }

    // Hiển thị độ ẩm
    private void DisplayHumidity() {

        tvHumidity = mView.findViewById(R.id.tv_humidity);

        if (humidityWebSocketClient == null) {
            humidityWebSocketClient = new HumidityWebSocketClient(this);
        }
    }

    // Hiển thị đột nhập
    private void DisplayBreakin() {
    }

    private void doorControl() {

        imgDoor = mView.findViewById(R.id.img_door);

        imgDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDoor();
            }
        });
    }

    private void handleDoor() {

        Intent intent = new Intent(getActivity(), DoorActivity.class);
        startActivity(intent);

    }

    private void DisplayWater() {

        if (waterWebSocketClient == null) {
            waterWebSocketClient = new WaterWebSocketClient(this);
        }

    }

    private void DisplayGas() {

        tvGas = mView.findViewById(R.id.tv_gas);

        if (gasWebSocketClient == null) {  // Chỉ tạo nếu chưa tồn tại
            gasWebSocketClient = new GasWebSocketClient(this);
        }
    }

    // Hiển thị lượng khí gas lên UI thông qua interface
    @Override
    public void onGasDataReceived(double gasLevel, String status) {
        requireActivity().runOnUiThread(() -> {
            tvGas.setText(String.format("%.2f ppm - %s", gasLevel, status));

            // Kiểm tra nếu khí gas vượt ngưỡng
            if (gasLevel > 1000) {
                showGasAlert(gasLevel, status);
            }
        });
    }

    // Hiển thị lượng khí nước lên UI thng qua interface
    // Nhận dữ liệu từ WebSocket
    @Override
    public void onWaterDataReceived(String status) {
        requireActivity().runOnUiThread(() -> {
            tvWater.setText(status);

            // Hiển thị cảnh báo nếu có rò rỉ nước
            if (status.equals("Có rò rỉ nước")) {
                showWaterAlert(status);
            }
        });
    }

    // interface xử lý logic hiển thị độ ẩm lên giao diện
    @Override
    public void onHumidityDataReceived(double humidityValue, String status) {
        requireActivity().runOnUiThread(() -> {
            tvHumidity.setText(String.format("Độ ẩm: %.0f%% - %s", humidityValue, status));

            // Hiển thị cảnh báo nếu độ ẩm quá cao
            if (humidityValue > 80) {
                showHumidityAlert(humidityValue, status);
            }
        });
    }

    // Hiển thị cảnh báo khí gas cao
    private void showGasAlert(double gasLevel, String status) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠ CẢNH BÁO KHÍ GAS ⚠")
                .setMessage("Nồng độ khí gas quá cao!\nGiá trị: " + gasLevel + " ppm\nTrạng thái: " + status)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Hiển thị cảnh báo nếu có rò rỉ nước
    private void showWaterAlert(String status) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠ CẢNH BÁO NGẬP LỤT ⚠")
                .setMessage("Trạng thái: " + status)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Hiển thị cảnh báo nếu độ ẩm quá cao
    private void showHumidityAlert(double humidityValue, String status) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠ CẢNH BÁO ĐỘ ẨM CAO ⚠")
                .setMessage("Độ ẩm quá cao!\nGiá trị: " + humidityValue + "%\nTrạng thái: " + status)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (gasWebSocketClient != null) {
            gasWebSocketClient.closeWebSocket();
            gasWebSocketClient = null; // Ngăn rò rỉ bộ nhớ
        }

        if (waterWebSocketClient != null) {
            waterWebSocketClient.closeWebSocket();
            waterWebSocketClient = null;
        }

        if (humidityWebSocketClient != null) {
            humidityWebSocketClient.closeWebSocket();
            humidityWebSocketClient = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "Reconnecting WebSocket in onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gasWebSocketClient != null) {
            gasWebSocketClient.closeWebSocket();
            Log.d("HomeFragment", "WebSocket Closed in onPause()");
        }

        if (waterWebSocketClient != null) {
            waterWebSocketClient.closeWebSocket();
        }

        if (humidityWebSocketClient != null) {
            humidityWebSocketClient.closeWebSocket();
        }
    }
}
