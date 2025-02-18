package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.SensorWater;
import com.user_manager_v1.repository.SensorWaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorWaterController {

    @Autowired
    private SensorWaterRepository repository;

    @GetMapping("/water")
    public List<SensorWater> getAllWaterData() {
        return repository.findAll();
    }
}
