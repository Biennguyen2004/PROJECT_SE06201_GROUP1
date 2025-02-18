package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.SensorClimate;
import com.user_manager_v1.repository.SensorClimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorClimateController {

    @Autowired
    private SensorClimateRepository repository;

    @GetMapping("/climate")
    public List<SensorClimate> getAllClimateData() {
        return repository.findAll();
    }
}
