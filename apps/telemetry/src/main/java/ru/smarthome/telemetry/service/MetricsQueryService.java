package ru.smarthome.telemetry.service;

import ru.smarthome.telemetry.model.MetricDto;

import java.util.List;
import java.util.Optional;

public interface MetricsQueryService {
    List<MetricDto> findLatestByLocationAndMetric(String houseId, String location, String metricCode);
    Optional<MetricDto> findLatestBySensor(String sensorId, boolean useExternalId);
}
