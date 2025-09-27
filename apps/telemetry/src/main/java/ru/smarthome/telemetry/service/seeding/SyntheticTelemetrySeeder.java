package ru.smarthome.telemetry.service.seeding;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import ru.smarthome.telemetry.event.SensorCreated;
import ru.smarthome.telemetry.event.SensorMeasuredEvent;
import ru.smarthome.telemetry.event.SensorRegistered;
import ru.smarthome.telemetry.repository.SensorJpaRepository;
import ru.smarthome.telemetry.repository.entity.SensorEntity;
import ru.smarthome.telemetry.service.SensorEventProcessor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class SyntheticTelemetrySeeder implements SensorEventProcessor {
    public static final String STATUS_BY_DEFAULT = "inactive";

    private final SensorJpaRepository repo;
    private final TelemetryProcessorSynthetic ingest;
    private final MetricValueGenerator generator;
    private final int count;
    private final int minutesStep;
    private final boolean enabled;

    public SyntheticTelemetrySeeder(
            SensorJpaRepository repo,
            TelemetryProcessorSynthetic ingest,
            MetricValueGenerator generator
    ) {
        this.repo = repo;
        this.ingest = ingest;
        this.generator = generator;
        this.count = 10;
        this.minutesStep = 1;
        this.enabled = true;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSensorRegistered(SensorRegistered evt) {
        if (!enabled) return;

        var metric = (evt.sensorType() == null) ? "generic" : evt.sensorType().toLowerCase();
        var unit   = (evt.unit() == null) ? defaultUnit(metric) : evt.unit();

        var now = Instant.now();
        for (int i = count - 1; i >= 0; i--) {
            var at = now.minus((long) i * minutesStep, ChronoUnit.MINUTES);
            var value = generator.next(metric);
            // КЛЮЧЕВОЕ: генерим событие и передаём в consumer
            ingest.onSensorMeasured(new SensorMeasuredEvent(evt.sensorId(), metric, value, unit, at));
        }
    }

    @Override
    public SensorRegistered onSensorCreated(SensorCreated sensorCreated) {
        String externalSensorId = sensorCreated.externalSensorId();
        var entity = repo.findByExternalSensorId(externalSensorId)
                .orElseGet(() -> {
                    var e = new SensorEntity();
                    e.setId(UUID.randomUUID());
                    e.setExternalSensorId(externalSensorId);
                    e.setStatus(STATUS_BY_DEFAULT);
                    return e;
                });

        // обновляем поля (правило: если пришло null — не затираем существующее)
        entity.setSensorType(sensorCreated.sensorType());
        if (sensorCreated.unit() != null) {
            entity.setUnit(sensorCreated.unit());
        }
        if (sensorCreated.location() != null) {
            entity.setLocation(sensorCreated.location());
        }
        if (sensorCreated.sensorName() != null) {
            entity.setSensorName(sensorCreated.sensorName());
        }

        // Устанавливаем статус, если он передан
        if (sensorCreated.status() != null && !sensorCreated.status().isBlank()) {
            entity.setStatus(sensorCreated.status());
        } else if (entity.getStatus().equals(STATUS_BY_DEFAULT)) {
            entity.setStatus("active");
        }

        entity = repo.save(entity);

        return new SensorRegistered(
                entity.getId(),
                entity.getExternalSensorId(),
                entity.getSensorType(),
                entity.getUnit(),
                entity.getLocation(),
                entity.getSensorName(),
                entity.getStatus()
        );
    }

    private String defaultUnit(String metric) {
        return switch (metric) {
            case "temperature" -> "C";
            case "humidity"    -> "%";
            case "power"       -> "W";
            default            -> "";
        };
    }
}

