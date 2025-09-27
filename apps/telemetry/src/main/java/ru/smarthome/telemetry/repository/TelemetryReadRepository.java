package ru.smarthome.telemetry.repository;

import ru.smarthome.telemetry.model.MetricDto;

import java.util.List;
import java.util.Optional;

public interface TelemetryReadRepository {
    List<MetricDto> findLatestByLocationAndMetric(String houseId, String location, String metricCode);
    Optional<MetricDto> findLatestBySensorByInternalId(String sensorUuid);
    Optional<MetricDto> findLatestBySensorByExternalId(String externalSensorId);
}