package com.example.smarthome.network;

import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

public class GasWebSocketClient {
    private static final String TAG = "WebSocket";
    private static final String WEBSOCKET_URL = "wss://b8a9-1-55-211-160.ngrok-free.app/ws/sensor/gas";
    private WebSocket webSocket;
    private GasWebSocketListener listener;

    public GasWebSocketClient(GasWebSocketListener listener) {
        this.listener = listener;
        connectWebSocket();
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);

                    // Kiểm tra nếu JSON có chứa key "value"
                    if (jsonObject.has("value")) {
                        double gasLevel = jsonObject.getDouble("value");

                        // Lấy trạng thái (status)
                        String status = jsonObject.has("status") ? jsonObject.getString("status") : "unknown";

                        // Gửi dữ liệu về listener
                        if (listener != null) {
                            listener.onGasDataReceived(gasLevel, status);
                        }

                    } else {
                        Log.e(TAG, "Invalid JSON: missing 'value' field");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error: " + t.getMessage());

                // Tự động reconnect sau 5 giây
                new android.os.Handler().postDelayed(() -> GasWebSocketClient.this.connectWebSocket(), 5000);
            }
        });
    }

    public interface GasWebSocketListener {
        void onGasDataReceived(double gasLevel, String status);
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
            webSocket = null;
            Log.d(TAG, "WebSocket Closed");
        }
    }
}
