package com.user_manager_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.user_manager_v1.models.SensorRelay;

public interface SensorRelayRepository extends JpaRepository<SensorRelay, Long> {
}
