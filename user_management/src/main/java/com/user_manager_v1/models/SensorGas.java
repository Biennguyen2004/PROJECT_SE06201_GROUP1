package com.user_manager_v1.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SensorGas")
public class SensorGas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "gas_value", nullable = false)
    private int gasValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SensorGas() {}

    public SensorGas(String deviceId, int gasValue) {
        this.deviceId = deviceId;
        this.gasValue = gasValue;
        this.createdAt = LocalDateTime.now(); // Lưu thời gian hiện tại
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public int getGasValue() { return gasValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
