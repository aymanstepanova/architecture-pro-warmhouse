package ru.smarthome.temperature.service;

import ru.smarthome.temperature.model.TemperatureReading;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Random;

@Service
public class TemperatureService {
    public static final String UNIT = "C";
    private final TemperatureGenerator temperatureGenerator;
    private final Random rnd = new Random();

    public TemperatureService(TemperatureGenerator temperatureGenerator) {
        this.temperatureGenerator = temperatureGenerator;
    }

    public TemperatureReading read(String location) {
        double value = temperatureGenerator.getTemperature(location);
        String sensorId = getSensorIdByLocation(location);

        return new TemperatureReading(
                sensorId,
                location,
                value,
                UNIT,
                OffsetDateTime.now()
        );
    }

    public TemperatureReading readBySensorId(String sensorId) {
        double value = temperatureGenerator.getTemperature(sensorId);
        String location = getLocationBySensorId(sensorId);

        return new TemperatureReading(
                sensorId,
                location,
                value,
                UNIT,
                OffsetDateTime.now()
        );
    }

    private static String getSensorIdByLocation(String location) {
        return switch (location) {
            case "Living Room" -> "1";
            case "Bedroom" -> "2";
            case "Kitchen" -> "3";
            default -> "0";
        };
    }

    private static String getLocationBySensorId(String sensorId) {
        return switch (sensorId) {
            case "1" -> "Living Room";
            case "2" -> "Bedroom";
            case "3" -> "Kitchen";
            default -> "Unknown";
        };
    }
}
