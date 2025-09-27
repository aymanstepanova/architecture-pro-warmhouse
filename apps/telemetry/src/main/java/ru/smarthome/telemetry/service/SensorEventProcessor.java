package ru.smarthome.telemetry.service;

import ru.smarthome.telemetry.event.SensorCreated;
import ru.smarthome.telemetry.event.SensorRegistered;

/**
 * Обработчик событий сенсора. Источником может быть само приложение, монолит или брокер
 */
public interface SensorEventProcessor {
    void onSensorRegistered(SensorRegistered sensorRegistered);
    SensorRegistered onSensorCreated(SensorCreated sensorCreated);
}
