package com.user_manager_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.user_manager_v1.models.SensorGas;

public interface SensorDataRepository extends JpaRepository<SensorGas, Long> {
}
