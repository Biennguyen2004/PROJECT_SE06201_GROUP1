package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.SensorDoor;
import com.user_manager_v1.repository.SensorDoorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorDoorController {

    @Autowired
    private SensorDoorRepository repository;

    @GetMapping("/door")
    public List<SensorDoor> getAllDoorData() {
        return repository.findAll();
    }
}
