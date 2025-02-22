package com.example.smarthome.ui.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthome.R;
import com.example.smarthome.network.GasWebSocketClient;

public class HomeFragment extends Fragment implements GasWebSocketClient.GasWebSocketListener {

    private View mView;
    private TextView tvGas;
    private GasWebSocketClient gasWebSocketClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        DisplayGas();



        return mView;
    }

    private void DisplayGas() {

        tvGas = mView.findViewById(R.id.tv_gas);
        // Kết nối WebSocket
        gasWebSocketClient = new GasWebSocketClient(this);
    }

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

    // Hiển thị cảnh báo khí gas cao
    private void showGasAlert(double gasLevel, String status) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠ CẢNH BÁO KHÍ GAS ⚠")
                .setMessage("Nồng độ khí gas quá cao!\nGiá trị: " + gasLevel + " ppm\nTrạng thái: " + status)
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
