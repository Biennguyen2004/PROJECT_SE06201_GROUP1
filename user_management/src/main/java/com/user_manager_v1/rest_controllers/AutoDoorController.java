package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.dto.ApiResponse;
import com.user_manager_v1.models.AutoDoorStatusRequest;
import com.user_manager_v1.services.MqttPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/door")
public class AutoDoorController {
    @Autowired
    private MqttPublisherService mqttPublisherService;

    @PostMapping("/auto")
    public ResponseEntity<ApiResponse<String>> controlAutoDoor(@RequestBody AutoDoorStatusRequest request) {
        String command = request.isStatus() ? "batautocua" : "tatautocua";
        boolean isSent = mqttPublisherService.sendCommandToMQTT("openauto/door", command);

        if (isSent) {
            return ResponseEntity.ok(new ApiResponse<>(true, " Auto Door Command Sent", command));
        } else {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, " Failed to send auto command", null));
        }
    }
}
