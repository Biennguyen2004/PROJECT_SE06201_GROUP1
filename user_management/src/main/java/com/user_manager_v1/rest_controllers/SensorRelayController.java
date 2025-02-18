package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.SensorRelay;
import com.user_manager_v1.repository.SensorRelayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorRelayController {

    @Autowired
    private SensorRelayRepository repository;

    @GetMapping("/relay")
    public List<SensorRelay> getAllRelayData() {
        return repository.findAll();
    }
}
