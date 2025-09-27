package ru.smarthome.telemetry.service;

import ru.smarthome.telemetry.model.SensorCreateRequest;
import ru.smarthome.telemetry.model.SensorResponse;

public interface SensorCommandService {
    SensorResponse createOrUpdate(SensorCreateRequest req);
}

