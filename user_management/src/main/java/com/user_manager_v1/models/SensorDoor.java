package com.user_manager_v1.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SensorDoor")
public class SensorDoor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "distance", nullable = false)
    private int distance; // Khoảng cách đo được

    @Column(name = "status", nullable = false)
    private String status; // "open" hoặc "closed"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SensorDoor() {}

    public SensorDoor(String deviceId, String location, int distance, String status) {
        this.deviceId = deviceId;
        this.location = location;
        this.distance = distance;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getLocation() { return location; }
    public int getDistance() { return distance; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
