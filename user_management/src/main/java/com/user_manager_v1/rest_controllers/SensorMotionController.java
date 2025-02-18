package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.SensorMotion;
import com.user_manager_v1.repository.SensorMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorMotionController {

    @Autowired
    private SensorMotionRepository repository;

    @GetMapping("/motion")
    public List<SensorMotion> getAllMotionData() {
        return repository.findAll();
    }
}
