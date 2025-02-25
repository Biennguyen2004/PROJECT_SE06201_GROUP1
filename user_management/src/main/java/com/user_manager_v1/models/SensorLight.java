package com.user_manager_v1.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SensorLight")
public class SensorLight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "value", nullable = false)
    private int value; // 0: Tối, 1: Sáng

    @Column(name = "status", nullable = false)
    private String status; // "dark" hoặc "bright"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SensorLight() {}

    public SensorLight(String deviceId, String location, int value, String status) {
        this.deviceId = deviceId;
        this.location = location;
        this.value = value;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getLocation() { return location; }
    public int getValue() { return value; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
