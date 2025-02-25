package com.user_manager_v1.repository;

import com.user_manager_v1.models.SensorGas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorGasRepository extends JpaRepository<SensorGas, Long> {
    Optional<SensorGas> findByDeviceId(String deviceId);
}