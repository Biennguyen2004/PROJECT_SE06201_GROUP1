package com.user_manager_v1.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "sensor_gas")

public class SensorGas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private String location;
    private int value;  // Giá trị đo của cảm biến khí gas
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SensorGas() {}

    public SensorGas(String deviceId, String location, int value, String status) {
        this.deviceId = deviceId;
        this.location = location;
        this.value = value;
        this.status = status;
        this.createdAt = LocalDateTime.now(); // Lưu thời gian hiện tại
    }

    // Getter & Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
