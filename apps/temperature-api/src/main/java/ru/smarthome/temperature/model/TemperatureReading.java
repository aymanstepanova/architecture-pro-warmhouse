package ru.smarthome.temperature.model;

import java.time.OffsetDateTime;

public record TemperatureReading(
        String sensorId,
        String location,
        double value,
        String unit,
        OffsetDateTime timestamp
) {}
