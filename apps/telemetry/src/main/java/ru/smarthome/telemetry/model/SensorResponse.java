package ru.smarthome.telemetry.model;

import java.util.UUID;

public record SensorResponse(
        UUID id, String sensorId, String sensorType, String unit, String location, String status, String sensorName
) {}