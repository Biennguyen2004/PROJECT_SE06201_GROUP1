package com.user_manager_v1.services;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.user_manager_v1.models.SensorGas;
import com.user_manager_v1.repository.SensorDataRepository;
import com.user_manager_v1.utils.SslUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MqttSubscriberService {
    private final String broker = "ssl://b2ab94290f9846df90474d2bb8772308.s1.eu.hivemq.cloud:8883";
    // HiveMQ Cloud Broker
    private final String clientId = "SpringBootMQTTClient";
    private final String topic = "home/fire_alert"; // Thay bằng topic thực tế của bạn
    private final String username = "nguyenbien"; // Thay bằng username của bạn
    private final String password = "4!xH7QbgfmUXhge"; // Thay bằng password của bạn

    @Autowired
    private SensorDataRepository sensorDataRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Dùng để parse JSON

    public MqttSubscriberService() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setSocketFactory(SslUtil.getSocketFactory());

            MqttClient mqttClient = new MqttClient(broker, clientId);
            mqttClient.connect(options);
            mqttClient.subscribe(topic, (t, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Received JSON: " + payload);

                try {
                    // Parse JSON
                    JsonNode jsonNode = objectMapper.readTree(payload);
                    String deviceId = jsonNode.get("device_id").asText();
                    int gasValue = jsonNode.get("gas_value").asInt();

                    // Lưu vào database
                    SensorGas data = new SensorGas(deviceId, gasValue);
                    sensorDataRepository.save(data);
                    System.out.println("Saved to DB: " + data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SSL Configuration Error: " + e.getMessage());
        }
    }
}