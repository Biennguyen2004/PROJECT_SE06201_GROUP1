package com.user_manager_v1.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SensorClimate")
public class SensorClimate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "temperature", nullable = false)
    private double temperature;

    @Column(name = "humidity", nullable = false)
    private double humidity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SensorClimate() {}

    public SensorClimate(String deviceId, String location, double temperature, double humidity) {
        this.deviceId = deviceId;
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getLocation() { return location; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
