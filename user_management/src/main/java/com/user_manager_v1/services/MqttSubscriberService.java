package com.user_manager_v1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_manager_v1.models.*;
import com.user_manager_v1.repository.*;
import com.user_manager_v1.utils.SslUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class MqttSubscriberService {
    private final String broker = "ssl://b2ab94290f9846df90474d2bb8772308.s1.eu.hivemq.cloud:8883";
    private final String clientId = "SpringBootMQTTClient";
    private final String username = "nguyenbien";
    private final String password = "4!xH7QbgfmUXhge";

    @Autowired
    private SensorGasRepository sensorGasRepository;

    @Autowired
    private SensorClimateRepository sensorClimateRepository;

    @Autowired
    private SensorMotionRepository sensorMotionRepository;


    @Autowired
    private SensorWaterRepository sensorWaterRepository;

    @Autowired
    private SensorLightRepository sensorLightRepository;

    @Autowired
    private SensorDoorRepository sensorDoorRepository;

    @Autowired
    private SensorRelayRepository sensorRelayRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttSubscriberService() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setSocketFactory(SslUtil.getSocketFactory());

            MqttClient mqttClient = new MqttClient(broker, clientId);
            mqttClient.connect(options);

            // Đăng ký tất cả các topic
            mqttClient.subscribe("smarthome/kitchen/gas/esp32_01", this::handleGasSensor);
            mqttClient.subscribe("smarthome/kitchen/climate/esp32_02", this::handleClimateSensor);
            mqttClient.subscribe("smarthome/living_room/motion/esp32_03", this::handleMotionSensor);
            mqttClient.subscribe("smarthome/bathroom/water/esp32_04", this::handleWaterSensor);
            mqttClient.subscribe("smarthome/living_room/light/esp32_05", this::handleLightSensor);
            mqttClient.subscribe("smarthome/main_door/door/esp32_06", this::handleDoorSensor);
            mqttClient.subscribe("smarthome/living_room/relay/esp32_07", this::handleRelaySensor);

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SSL Configuration Error: " + e.getMessage());
        }
    }

    // Xử lý dữ liệu cảm biến khí gas
    @Transactional
    public void handleGasSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorGas sensorGas = new SensorGas();
            sensorGas.setDeviceId(deviceId);
            sensorGas.setLocation(location);
            sensorGas.setValue(value);
            sensorGas.setStatus(status);

            sensorGasRepository.save(sensorGas);
            System.out.println("Saved Gas Sensor Data: " + sensorGas);

        } catch (Exception e) {
            System.err.println("Error processing gas sensor data: " + e.getMessage());
        }
    }

    // Xử lý dữ liệu cảm biến nhiệt độ & độ ẩm
    @Transactional
    private void handleClimateSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Received Climate Sensor Data: " + payload);

            JsonNode jsonNode = objectMapper.readTree(payload);
            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            double temperature = jsonNode.get("temperature").asDouble();
            double humidity = jsonNode.get("humidity").asDouble();

            SensorClimate data = new SensorClimate(deviceId, location, temperature, humidity);
            sensorClimateRepository.save(data);
            System.out.println("Saved Climate Data to DB: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xử lý dữ liệu cảm biến chuyển động
    @Transactional
    private void handleMotionSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Received Motion Sensor Data: " + payload);

            JsonNode jsonNode = objectMapper.readTree(payload);
            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorMotion data = new SensorMotion(deviceId, location, value, status);
            sensorMotionRepository.save(data);
            System.out.println("Saved Motion Data to DB: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void handleWaterSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Received Water Sensor Data: " + payload);

            JsonNode jsonNode = objectMapper.readTree(payload);
            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorWater data = new SensorWater(deviceId, location, value, status);
            sensorWaterRepository.save(data);
            System.out.println("Saved Water Data to DB: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void handleLightSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Received Light Sensor Data: " + payload);

            JsonNode jsonNode = objectMapper.readTree(payload);
            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorLight data = new SensorLight(deviceId, location, value, status);
            sensorLightRepository.save(data);
            System.out.println("Saved Light Data to DB: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Xử lý dữ liệu cảm biến cửa

    @Transactional
    private void handleDoorSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Received Door Sensor Data: " + payload);

            JsonNode jsonNode = objectMapper.readTree(payload);
            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int distance = jsonNode.get("distance").asInt();
            String status = jsonNode.get("status").asText();

            SensorDoor data = new SensorDoor(deviceId, location, distance, status);
            sensorDoorRepository.save(data);
            System.out.println("Saved Door Data to DB: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xử lý dữ liệu relay

    @Transactional
    private void handleRelaySensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Received Relay Sensor Data: " + payload);

            JsonNode jsonNode = objectMapper.readTree(payload);
            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            String status = jsonNode.get("status").asText();

            SensorRelay data = new SensorRelay(deviceId, location, status);
            sensorRelayRepository.save(data);
            System.out.println("Saved Relay Data to DB: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
