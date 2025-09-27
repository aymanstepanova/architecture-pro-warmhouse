package ru.smarthome.telemetry.event;

import java.util.UUID;

/**
 * Событие регистрации датчика
 * В будущем будет использоваться для подписки на метрики при создании датчика
 */
public record SensorRegistered(
        UUID sensorId,
        String externalSensorId,
        String sensorType,
        String unit,
        String location,
        String sensorName,
        String status
) {}
