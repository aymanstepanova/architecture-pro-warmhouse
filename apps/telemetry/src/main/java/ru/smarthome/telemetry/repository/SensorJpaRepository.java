package ru.smarthome.telemetry.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.smarthome.telemetry.repository.entity.SensorEntity;

import java.util.Optional;
import java.util.UUID;

public interface SensorJpaRepository extends JpaRepository<SensorEntity, UUID> {
    Optional<SensorEntity> findByExternalSensorId(String externalSensorId);
}

