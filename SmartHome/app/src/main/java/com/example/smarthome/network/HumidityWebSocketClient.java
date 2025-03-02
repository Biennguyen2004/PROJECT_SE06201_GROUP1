package com.example.smarthome.network;

import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

public class HumidityWebSocketClient {
    private static final String TAG = "HumidityWebSocket";
    private static final String WEBSOCKET_URL = "wss://bae4-2001-ee0-40e1-9178-89cc-15a5-152e-b1.ngrok-free.app/ws/sensor/humidity";
    private WebSocket webSocket;
    private HumidityWebSocketListener listener;

    private static final int MAX_RETRY = 5;
    private int retryCount = 0;

    public HumidityWebSocketClient(HumidityWebSocketListener listener) {
        this.listener = listener;
        connectWebSocket();
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected Humidity");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);

                    if (jsonObject.has("humidity_value")) {
                        double humidityValue = jsonObject.getDouble("humidity_value");
                        String status = calculateHumidityStatus(humidityValue);

                        if (listener != null) {
                            listener.onHumidityDataReceived(humidityValue, status);
                        }
                    } else {
                        Log.e(TAG, "Invalid JSON: Missing 'humidity_value' field");
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

    private String calculateHumidityStatus(double humidityValue) {
        if (humidityValue > 80) {
            return "Cao";
        } else if (humidityValue >= 50) {
            return "Trung bình";
        } else {
            return "Thấp";
        }
    }

    public interface HumidityWebSocketListener {
        void onHumidityDataReceived(double humidityValue, String status);
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
            webSocket = null;
            Log.d(TAG, "WebSocket Closed");
        }
    }
}
