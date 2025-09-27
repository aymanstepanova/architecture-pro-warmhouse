package ru.smarthome.telemetry.service;


import lombok.RequiredArgsConstructor;
import ru.smarthome.telemetry.model.MetricDto;
import ru.smarthome.telemetry.repository.TelemetryReadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetricsQueryServiceImpl implements MetricsQueryService {

    private final TelemetryReadRepository telemetryReadRepository;

    public MetricsQueryServiceImpl(TelemetryReadRepository telemetryReadRepository) {
        this.telemetryReadRepository = telemetryReadRepository;
    }

    @Override
    public List<MetricDto> findLatestByLocationAndMetric(String location, String metricCode) {
        return telemetryReadRepository.findLatestByLocationAndMetric(location, metricCode);
    }

    @Override
    public Optional<MetricDto> findLatestBySensor(String sensorId, boolean useExternalId) {
        return useExternalId
                ? telemetryReadRepository.findLatestBySensorByExternalId(sensorId)
                : telemetryReadRepository.findLatestBySensorByInternalId(sensorId);
    }
}

