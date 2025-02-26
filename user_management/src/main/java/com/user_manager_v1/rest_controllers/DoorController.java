package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.dto.ApiResponse;
import com.user_manager_v1.models.DoorStatusRequest;
import com.user_manager_v1.services.MqttPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/door")
public class DoorController {

    @Autowired
    private MqttPublisherService mqttPublisherService;

    @PostMapping("/control")
    public ResponseEntity<ApiResponse<String>> controlDoor(@RequestBody DoorStatusRequest request) {
        String command = request.isStatus() ? "mocua" : "dongcua";
        String topic = "openclose/door";

        System.out.println("🔍 [API] Received Door Status: " + request.isStatus());

        // 📡 Gửi tín hiệu lên HiveMQ qua MQTT Client
        boolean isSent = mqttPublisherService.sendCommandToMQTT(topic, command);

        if (isSent) {
            return ResponseEntity.ok(new ApiResponse<>(true, " Door Command Sent", command));
        } else {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, " Failed to send command", null));
        }
    }
}
