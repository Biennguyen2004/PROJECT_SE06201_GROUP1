package com.user_manager_v1.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private final String broker = "ssl://broker.hivemq.com:8883";
    private final String clientId = "SpringBootMQTTClient";
    private final String username = "biendeptrai";
    private final String password = "FZ6VQ@hfFYQyTe2";

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(broker, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        client.connect(options);
        System.out.println("✅ [MQTT] Connected to HiveMQ Broker");
        return client;
    }
}
