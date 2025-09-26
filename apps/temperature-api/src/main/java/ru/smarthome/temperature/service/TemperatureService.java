package ru.smarthome.temperature.service;

import ru.smarthome.temperature.model.TemperatureReading;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class TemperatureService {

    private final Random rnd = new Random();

    public TemperatureReading read(String location) {
        // Имитация природы: -30..+45 °C, плюс лёгкое «смещение» от локации
        double value = getTemperature(location);

        return new TemperatureReading(
                "sensor-" + UUID.nameUUIDFromBytes(location.getBytes()),
                location,
                value,
                "C",
                OffsetDateTime.now()
        );
    }

    public TemperatureReading readBySensorId(String sensorId) {
        // Имитация природы: -30..+45 °C, плюс лёгкое «смещение» от локации
        double value = getTemperature(sensorId);

        return new TemperatureReading(
                sensorId,
                //обычно сервис знает, где находится датчик
                "Living Room",
                value,
                "C",
                OffsetDateTime.now()
        );
    }

    private double getTemperature(String sensorId) {
        double base = -30 + rnd.nextDouble() * 75;
        double bias = Math.tanh(sensorId.hashCode() / 10000.0) * 3; // -3..+3
        double value = Math.round((base + bias) * 10.0) / 10.0;
        return value;
    }
}
