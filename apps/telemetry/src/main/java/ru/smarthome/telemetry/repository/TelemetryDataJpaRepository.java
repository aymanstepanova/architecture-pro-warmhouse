package ru.smarthome.telemetry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smarthome.telemetry.repository.entity.TelemetryDataEntity;

public interface TelemetryDataJpaRepository extends JpaRepository<TelemetryDataEntity, Long> {
}
