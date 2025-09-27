package ru.smarthome.telemetry.event;

import lombok.Builder;

/**
 * Событие регистрации датчика
 * В будущем будет использоваться для подписки на метрики при создании датчика
 */
@Builder
public record SensorCreated(
        String externalSensorId,
        String sensorType,
        String unit,
        String location,
        String sensorName,
        String status
) {}
