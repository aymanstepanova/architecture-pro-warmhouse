package ru.smarthome.telemetry.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.smarthome.telemetry.model.SensorCreateRequest;
import ru.smarthome.telemetry.model.SensorResponse;
import ru.smarthome.telemetry.repository.SensorJpaRepository;
import ru.smarthome.telemetry.repository.entity.SensorEntity;

import java.util.UUID;

@Service
public class SensorCommandServiceImpl implements SensorCommandService {

    public static final String STATUS_BY_DEFAULT = "inactive";
    private final SensorJpaRepository repo;

    public SensorCommandServiceImpl(SensorJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public SensorResponse createOrUpdate(SensorCreateRequest req) {
        var status = (req.status() == null || req.status().isBlank()) ? "active" : req.status();

        var entity = repo.findByExternalSensorId(req.sensorId())
                .orElseGet(() -> {
                    var e = new SensorEntity();
                    e.setId(UUID.randomUUID());
                    e.setExternalSensorId(req.sensorId());
                    e.setStatus(STATUS_BY_DEFAULT);
                    return e;
                });

        // обновляем поля (правило: если пришло null — не затираем существующее)
        entity.setSensorType(req.sensorType());
        if (req.unit() != null)      entity.setUnit(req.unit());
        if (req.location() != null)  entity.setLocation(req.location());
        if (req.name() != null)  entity.setSensorName(req.name());

        entity = repo.save(entity);

        return new SensorResponse(
                entity.getId(),
                entity.getExternalSensorId(),
                entity.getSensorType(),
                entity.getUnit(),
                entity.getLocation(),
                entity.getStatus(),
                entity.getSensorName()
        );
    }
}