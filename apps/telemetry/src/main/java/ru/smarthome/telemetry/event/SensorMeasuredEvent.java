package ru.smarthome.telemetry.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SensorMeasuredEvent(
        UUID sensorId,          // PK telemetry.sensors.id
        String metricCode,      // "temperature"/"humidity"/...
        BigDecimal value,       // числовое значение
        String unit,            // "C", "%", ...
        Instant measuredAt      // когда измерено
) {}
