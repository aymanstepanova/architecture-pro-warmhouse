package ru.smarthome.telemetry.repository;

import ru.smarthome.telemetry.repository.entity.SensorEntity;

import java.util.UUID;

public interface SensorWriteRepository {
    SensorEntity upsert(String sensorId, UUID deviceId, String sensorType, String unit, String location, String status);
}