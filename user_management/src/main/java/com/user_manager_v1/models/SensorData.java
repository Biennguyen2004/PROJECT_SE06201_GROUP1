package com.user_manager_v1.models;

import jakarta.persistence.*;

@Entity
@Table(name = "sensor_data")
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    public SensorData() {}

    public SensorData(String data) {
        this.data = data;
    }

    public Long getId() { return id; }
    public String getData() { return data; }
}