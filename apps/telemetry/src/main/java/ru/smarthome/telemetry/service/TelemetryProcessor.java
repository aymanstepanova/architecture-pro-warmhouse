package ru.smarthome.telemetry.service;

import ru.smarthome.telemetry.event.SensorMeasuredEvent;

/**
 * Обработчик событий телеметрии датчика
 */
public interface TelemetryProcessor {
    void onSensorMeasured(SensorMeasuredEvent sensorMeasuredEvent);
}
