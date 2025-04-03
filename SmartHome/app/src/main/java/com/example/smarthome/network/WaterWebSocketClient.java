package com.example.smarthome.network;

import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

public class WaterWebSocketClient {
    private static final String TAG = "WaterWebSocket";
    private static final String WEBSOCKET_URL = "wss://8e1c-118-70-118-224.ngrok-free.app/ws/sensor/water";
    private WebSocket webSocket;
    private WaterWebSocketListener listener;

    private static final int MAX_RETRY = 5;
    private int retryCount = 0;

    public WaterWebSocketClient(WaterWebSocketListener listener) {
        this.listener = listener;
        connectWebSocket();
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected water");
                retryCount = 0; // Reset lại số lần thử lại khi kết nối thành công
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);

                    // Kiểm tra nếu JSON có chứa key "water_detected"
                    if (jsonObject.has("water_detected")) {
                        boolean waterDetected = jsonObject.getBoolean("water_detected");
                        String status = jsonObject.optString("status", "Không xác định");

                        // Chuyển đổi trạng thái
                        String waterStatus = waterDetected ? "Có rò rỉ nước" : "Không có rò rỉ";

                        // Gửi dữ liệu về listener
                        if (listener != null) {
                            listener.onWaterDataReceived(waterStatus);
                        }
                    } else {
                        Log.e(TAG, "Invalid JSON: missing 'water_detected' field");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error: " + t.getMessage());

                if (retryCount < MAX_RETRY) {
                    retryCount++;
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        Log.d(TAG, "Reconnecting WebSocket... Attempt " + retryCount);
                        connectWebSocket();
                    }, 5000);
                } else {
                    Log.e(TAG, "Max retry reached. WebSocket will not reconnect.");
                }
            }
        });
    }

    public interface WaterWebSocketListener {
        void onWaterDataReceived(String status);
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
            webSocket = null;
            Log.d(TAG, "WebSocket Closed");
        }
    }
}
