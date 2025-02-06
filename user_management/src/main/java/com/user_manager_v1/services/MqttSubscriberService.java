//package com.user_manager_v1.services;
//
//import org.eclipse.paho.client.mqttv3.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.user_manager_v1.models.SensorData;
//import com.user_manager_v1.repository.SensorDataRepository;
//import com.user_manager_v1.utils.SslUtil;
//
//@Service
//public class MqttSubscriberService {
//    private final String broker = "ssl://b2ab94290f9846df90474d2bb8772308.s1.eu.hivemq.cloud:8883";
//    // HiveMQ Cloud Broker
//    private final String clientId = "SpringBootMQTTClient";
//    private final String topic = "home/fire_alert"; // Thay bằng topic thực tế của bạn
//    private final String username = "nguyenbien"; // Thay bằng username của bạn
//    private final String password = "4!xH7QbgfmUXhge"; // Thay bằng password của bạn
//
//    @Autowired
//    private SensorDataRepository sensorDataRepository;
//
//    public MqttSubscriberService() {
//        try {
//            MqttConnectOptions options = new MqttConnectOptions();
//            options.setUserName(username);
//            options.setPassword(password.toCharArray());
//            try {
//                options.setSocketFactory(SslUtil.getSocketFactory());
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("SSL Configuration Failed: " + e.getMessage());
//            }
//            // Dùng SSL để kết nối an toàn
//
//            MqttClient mqttClient = new MqttClient(broker, clientId);
//            mqttClient.connect(options);
//            mqttClient.subscribe(topic, (t, message) -> {
//                String payload = new String(message.getPayload());
//                System.out.println("Received: " + payload);
//
//                // Lưu dữ liệu vào database
//                SensorData data = new SensorData(payload);
//                sensorDataRepository.save(data);
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//}
