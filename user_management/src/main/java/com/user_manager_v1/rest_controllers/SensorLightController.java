package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.SensorLight;
import com.user_manager_v1.repository.SensorLightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorLightController {

    @Autowired
    private SensorLightRepository repository;

    @GetMapping("/light")
    public List<SensorLight> getAllLightData() {
        return repository.findAll();
    }
}
