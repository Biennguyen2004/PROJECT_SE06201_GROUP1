package com.example.smarthome.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthome.R;
import com.example.smarthome.network.GasWebSocketClient;
import com.example.smarthome.network.WaterWebSocketClient;
import com.example.smarthome.ui.device.DoorActivity;

public class HomeFragment extends Fragment implements GasWebSocketClient.GasWebSocketListener, WaterWebSocketClient.WaterWebSocketListener {

    private View mView;

    private TextView tvGas;
    private ImageView imgLinght;
    private GasWebSocketClient gasWebSocketClient;

    private TextView tvWater;
    private WaterWebSocketClient waterWebSocketClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

//        DisplayGas();

//        DisplayWater();

        doorControl();

        return mView;
    }

    private void doorControl() {

        imgLinght = mView.findViewById(R.id.img_light);

        imgLinght.setOnClickListener(new View.OnClickListener() {
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

        tvWater = mView.findViewById(R.id.tv_display_water);

        // Kết nối WebSocket
        waterWebSocketClient = new WaterWebSocketClient(this);

    }

    private void DisplayGas() {

        tvGas = mView.findViewById(R.id.tv_gas);
        // Kết nối WebSocket
        gasWebSocketClient = new GasWebSocketClient(this);
    }

    // Hiển thị lượng khí gas lên UI thng qua interface
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
    @Override
    public void onWaterDataReceived(double waterLevel, String status) {
        requireActivity().runOnUiThread(() -> {
            tvWater.setText(String.format("%.2f m - %s", waterLevel, status));

            // Kiểm tra nếu mực nước quá cao
            if (waterLevel > 1.5) { // Ví dụ: cảnh báo khi nước > 1.5m
                showWaterAlert(waterLevel, status);
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

    // Hiển thị cảnh báo nếu mực nước quá cao
    private void showWaterAlert(double waterLevel, String status) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠ CẢNH BÁO NGẬP LỤT ⚠")
                .setMessage("Mực nước quá cao!\nGiá trị: " + waterLevel + " m\nTrạng thái: " + status)
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
    }
}
