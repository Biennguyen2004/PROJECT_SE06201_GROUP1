package com.user_manager_v1.services;

import com.user_manager_v1.utils.SslUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisherService {

    private final String broker = "ssl://506956350ef1467185352df5050e3aa7.s1.eu.hivemq.cloud:8883";
    private final String clientId = "SpringBootMQTTClient";
    private final String username = "biendeptrai";
    private final String password = "FZ6VQ@hfFYQyTe2";
    private MqttClient mqttClient;

    public MqttPublisherService() {
        connectToMQTT();
    }

    // 📌 Kết nối HiveMQ
    private void connectToMQTT() {
        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(username);
                options.setPassword(password.toCharArray());
                options.setSocketFactory(SslUtil.getSocketFactory());
                options.setCleanSession(false);
                options.setAutomaticReconnect(true);
                options.setConnectionTimeout(10);
                options.setKeepAliveInterval(20);

                mqttClient = new MqttClient(broker, clientId);
                mqttClient.connect(options);

                System.out.println("✅ [MQTT] Connected to HiveMQ Broker");
            }
        } catch (MqttException e) {
            System.err.println("❌ [MQTT] Failed to connect: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ [ERROR] Unexpected Exception: " + e.getMessage());
        }
    }

    // 📡 Gửi tin nhắn đến MQTT
    public boolean sendCommandToMQTT(String topic, String payload) {
        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                System.out.println("⚠️ [MQTT] Client not connected. Reconnecting...");
                connectToMQTT();
            }

            if (mqttClient.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                mqttClient.publish(topic, message);
                System.out.println("📡 [MQTT] Sent: " + payload + " to " + topic);
                return true;
            } else {
                System.err.println("❌ [MQTT] Still not connected after retry.");
                return false;
            }

        } catch (MqttException e) {
            System.err.println("❌ [MQTT] Failed to send: " + e.getMessage());
            return false;
        }
    }
}
