package com.example.smarthome.network;

import android.os.Handler;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

public class GasWebSocketClient {
    private static final String TAG = "GasSocket";
    private static final String WEBSOCKET_URL = "wss://8e1c-118-70-118-224.ngrok-free.app/ws/sensor/gas";
    private WebSocket webSocket;
    private GasWebSocketListener listener;
    private OkHttpClient client;
    private int retryCount = 0;
    private final int MAX_RETRY = 5;  // Giới hạn retry
    private final Handler handler = new Handler();  // Xử lý reconnect

    public GasWebSocketClient(GasWebSocketListener listener) {
        this.listener = listener;
        this.client = new OkHttpClient();
        connectWebSocket();
    }

    private void connectWebSocket() {
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected gas");
                retryCount = 0;  // Reset số lần retry khi kết nối thành công
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);

                    // Kiểm tra nếu JSON có chứa key "gas_value"
                    if (jsonObject.has("gas_value")) {
                        double gasLevel = jsonObject.getDouble("gas_value");

                        // Lấy trạng thái (status)
                        String status = jsonObject.optString("status", "unknown");

                        // Gửi dữ liệu về listener
                        if (listener != null) {
                            listener.onGasDataReceived(gasLevel, status);
                        }

                    } else {
                        Log.e(TAG, "Invalid JSON: missing 'gas_value' field");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error: " + t.getMessage());

                // Giới hạn số lần retry để tránh vòng lặp vô hạn
                if (retryCount < MAX_RETRY) {
                    retryCount++;
                    Log.d(TAG, "Reconnecting... Attempt " + retryCount);
                    handler.postDelayed(GasWebSocketClient.this::connectWebSocket, 5000);
                } else {
                    Log.e(TAG, "Max retry reached. WebSocket will not reconnect.");
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closing: " + reason);
                webSocket.close(1000, null);
            }
        });
    }

    public interface GasWebSocketListener {
        void onGasDataReceived(double gasLevel, String status);
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Activity Closed");
            webSocket = null;
            Log.d(TAG, "🛑 WebSocket Closed");
        }
    }



}
