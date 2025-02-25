package com.user_manager_v1.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_manager_v1.repository.*;
import com.user_manager_v1.utils.SslUtil;
import com.user_manager_v1.websocket.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Service
public class MqttSubscriberService {
    private final String broker = "ssl://506956350ef1467185352df5050e3aa7.s1.eu.hivemq.cloud:8883";
    private final String clientId = "SpringBootMQTTClient";
    private final String username = "biendeptrai";
    private final String password = "FZ6VQ@hfFYQyTe2";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

    public MqttSubscriberService(RedisTemplate<String, Object> redisTemplate, SensorClimateRepository sensorClimateRepository) {
        this.redisTemplate = redisTemplate;
        this.sensorClimateRepository = sensorClimateRepository;
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

    private void handleGasSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorGas sensorGas = new SensorGas(deviceId, location, value, status);

            // ✅ Lưu vào Redis (Tăng TTL lên 60 giây)
            redisTemplate.opsForValue().set("sensorGas:" + deviceId, sensorGas, 60, TimeUnit.SECONDS);

            // ✅ Lưu ngay vào MySQL
            sensorGasRepository.save(sensorGas);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorGas);

            // ✅ Gửi dữ liệu real-time qua WebSocket
            SensorGasWebSocketHandler.sendToAllClients(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClimateSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            double temperature = jsonNode.get("temperature").asDouble();
            double humidity = jsonNode.get("humidity").asDouble();

            SensorClimate sensorClimate = new SensorClimate(deviceId, location, temperature, humidity);

            // Lưu vào Redis cache trong 10 giây
            redisTemplate.opsForValue().set("sensorClimate:" + deviceId, sensorClimate, 60, TimeUnit.SECONDS);
            // ✅ Lưu ngay vào MySQL
            sensorClimateRepository.save(sensorClimate);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorClimate);
            // Gửi dữ liệu real-time qua WebSocket
            SensorClimateWebSocketHandler.sendToAllClients(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleDoorSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int distance = jsonNode.get("distance").asInt();
            String status = jsonNode.get("status").asText();

            SensorDoor sensorDoor = new SensorDoor(deviceId, location, distance, status);
            redisTemplate.opsForValue().set("sensorDoor:" + deviceId, sensorDoor, 60, TimeUnit.SECONDS);
            // ✅ Lưu ngay vào MySQL
            sensorDoorRepository.save(sensorDoor);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorDoor);
            SensorDoorWebSocketHandler.sendToAllClients(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleLightSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorLight sensorLight = new SensorLight(deviceId, location, value, status);
            redisTemplate.opsForValue().set("sensorLight:" + deviceId, sensorLight, 60, TimeUnit.SECONDS);
            // ✅ Lưu ngay vào MySQL
            sensorLightRepository.save(sensorLight);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorLight);
            SensorLightWebSocketHandler.sendToAllClients(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMotionSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorMotion sensorMotion = new SensorMotion(deviceId, location, value, status);
            redisTemplate.opsForValue().set("sensorMotion:" + deviceId, sensorMotion, 60, TimeUnit.SECONDS);
            // ✅ Lưu ngay vào MySQL
            sensorMotionRepository.save(sensorMotion);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorMotion);
            SensorMotionWebSocketHandler.sendToAllClients(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRelaySensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            String status = jsonNode.get("status").asText();

            SensorRelay sensorRelay = new SensorRelay(deviceId, location, status);
            redisTemplate.opsForValue().set("sensorRelay:" + deviceId, sensorRelay, 60, TimeUnit.SECONDS);
            // ✅ Lưu ngay vào MySQL
            sensorRelayRepository.save(sensorRelay);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorRelay);
            SensorRelayWebSocketHandler.sendToAllClients(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleWaterSensor(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);

            String deviceId = jsonNode.get("device_id").asText();
            String location = jsonNode.get("location").asText();
            int value = jsonNode.get("value").asInt();
            String status = jsonNode.get("status").asText();

            SensorWater sensorWater = new SensorWater(deviceId, location, value, status);

            redisTemplate.opsForValue().set("sensorWater:" + deviceId, sensorWater, 60, TimeUnit.SECONDS);
            // ✅ Lưu ngay vào MySQL
            sensorWaterRepository.save(sensorWater);
            System.out.println("✅ [DEBUG] Lưu ngay vào database: " + sensorWater);
            SensorWaterWebSocketHandler.sendToAllClients(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Định kỳ lấy dữ liệu từ Redis và lưu vào database
    @Scheduled(fixedRate = 5000) // Chạy mỗi 5 giây
    public void saveDataFromRedisToDB() {
        System.out.println("🔄 [DEBUG] Bắt đầu lưu dữ liệu từ Redis vào database...");

        saveSensorData("sensorGas:", sensorGasRepository);
        saveSensorData("sensorClimate:", sensorClimateRepository);
        saveSensorData("sensorDoor:", sensorDoorRepository);
        saveSensorData("sensorLight:", sensorLightRepository);
        saveSensorData("sensorMotion:", sensorMotionRepository);
        saveSensorData("sensorRelay:", sensorRelayRepository);
        saveSensorData("sensorWater:", sensorWaterRepository);

        System.out.println("✅ [DEBUG] Hoàn thành lưu dữ liệu từ Redis vào database.");
    }



    private <T> void saveSensorData(String prefix, JpaRepository<T, Long> repository) {
        System.out.println("🔍 [DEBUG] Kiểm tra Redis với prefix: " + prefix);

        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("⚠️ [DEBUG] Không có dữ liệu trong Redis với prefix: " + prefix);
            return;
        }

        for (String key : keys) {
            try {
                System.out.println("🔑 [DEBUG] Đang xử lý key: " + key);

                T sensorData = (T) redisTemplate.opsForValue().get(key);
                if (sensorData != null) {
                    System.out.println("💾 [DEBUG] Dữ liệu lấy từ Redis: " + sensorData);

                    repository.save(sensorData); // Lưu vào database
                    redisTemplate.delete(key);   // Xóa khỏi Redis

                    System.out.println("✅ [DEBUG] Đã lưu vào database và xóa khỏi Redis: " + key);
                } else {
                    System.out.println("⚠️ [DEBUG] Không lấy được dữ liệu từ Redis key: " + key);
                }
            } catch (Exception e) {
                System.err.println("❌ [ERROR] Lỗi khi lưu vào database: " + e.getMessage());
            }
        }
    }
}
