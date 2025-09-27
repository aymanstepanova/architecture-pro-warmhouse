package ru.smarthome.telemetry.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smarthome.telemetry.event.SensorCreated;
import ru.smarthome.telemetry.event.SensorRegistered;
import ru.smarthome.telemetry.model.SensorCreateRequest;
import ru.smarthome.telemetry.model.SensorResponse;

@Service
@RequiredArgsConstructor
public class SensorCommandServiceImpl implements SensorCommandService {

    private final SensorEventProcessor sensorEventProcessor;

    @Override
    @Transactional
    public SensorResponse createOrUpdate(SensorCreateRequest request) {
        SensorCreated sensorCreated = getSensorCreated(request);
        SensorRegistered sensorRegistered = sensorEventProcessor.onSensorCreated(sensorCreated);
        sensorEventProcessor.onSensorRegistered(sensorRegistered);

        return new SensorResponse(
                sensorRegistered.sensorId(),
                sensorRegistered.externalSensorId(),
                sensorRegistered.sensorType(),
                sensorRegistered.unit(),
                sensorRegistered.location(),
                sensorRegistered.status(),
                sensorRegistered.sensorName()
        );
    }

    private static SensorCreated getSensorCreated(SensorCreateRequest request) {
        return SensorCreated.builder()
                .sensorName(request.name())
                .externalSensorId(request.sensorId())
                .sensorType(request.sensorType())
                .unit(request.unit())
                .location(request.location())
                .build();
    }
}