package com.user_manager_v1.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SensorRelay")
public class SensorRelay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "status", nullable = false)
    private String status; // "on" hoặc "off"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SensorRelay() {}

    public SensorRelay(String deviceId, String location, String status) {
        this.deviceId = deviceId;
        this.location = location;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
