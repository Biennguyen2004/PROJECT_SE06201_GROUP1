package com.user_manager_v1.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.user_manager_v1.models.SensorGas;
import com.user_manager_v1.repository.SensorDataRepository;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorGasController {

    @Autowired
    private SensorDataRepository repository;

    @GetMapping("/gas")
    public List<SensorGas> getAllData() {
        return repository.findAll();
    }
}
