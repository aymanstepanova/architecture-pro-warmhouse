package ru.smarthome.telemetry.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SensorCreateRequest(
        @NotBlank @Size(max=100) String sensorId,
        UUID deviceId,
        @NotBlank @Size(max=50) String sensorType,
        @Size(max=16)  String unit,
        @Size(max=128) String location,
        @Size(max=30)  String status,
        @Size(max=30)  String name
) {}
