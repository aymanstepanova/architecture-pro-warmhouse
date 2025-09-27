package ru.smarthome.telemetry.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record MetricDto(
        String externalSensorId,   // sensors.sensor_id (внешний ключ)
        String sensorType,         // sensors.sensor_type
        String unit,               // sensors.unit
        String location,           // sensors.location
        String metricCode,         // telemetry_data.metric_code
        BigDecimal value,          // telemetry_data.sensor_value
        Map<String, Object> valueJson, // telemetry_data.value
        Instant measuredAt         // telemetry_data.measured_at
) {}
