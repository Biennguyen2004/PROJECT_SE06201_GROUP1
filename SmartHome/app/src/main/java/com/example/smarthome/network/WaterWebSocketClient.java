package com.example.smarthome.network;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

public class WaterWebSocketClient {
    private static final String TAG = "WaterWebSocket";
    private static final String WEBSOCKET_URL = "wss://b8a9-1-55-211-160.ngrok-free.app/ws/sensor/water";
    private WebSocket webSocket;
    private WaterWebSocketListener listener;

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
                Log.d(TAG, "WebSocket Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);

                    // Kiểm tra nếu JSON có chứa key "value"
                    if (jsonObject.has("value")) {
                        double waterLevel = jsonObject.optDouble("value", -1);
                        String status = jsonObject.optString("status", "unknown");

                        // Gửi dữ liệu về listener
                        if (listener != null) {
                            listener.onWaterDataReceived(waterLevel, status);
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
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    Log.d(TAG, "Reconnecting WebSocket...");
                    WaterWebSocketClient.this.connectWebSocket();
                }, 5000);
            }
        });
    }

    public interface WaterWebSocketListener {
        void onWaterDataReceived(double waterLevel, String status);
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
            webSocket = null;
            Log.d(TAG, "WebSocket Closed");
        }
    }
}
