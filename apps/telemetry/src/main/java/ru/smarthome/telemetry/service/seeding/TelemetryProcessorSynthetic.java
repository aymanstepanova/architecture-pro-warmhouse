package ru.smarthome.telemetry.service.seeding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smarthome.telemetry.event.SensorMeasuredEvent;
import ru.smarthome.telemetry.repository.TelemetryDataJpaRepository;
import ru.smarthome.telemetry.repository.entity.TelemetryDataEntity;
import ru.smarthome.telemetry.service.TelemetryProcessor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelemetryProcessorSynthetic implements TelemetryProcessor {

    private final TelemetryDataJpaRepository telemetryRepository;


    /**
     * Универсальный вход: принимает уже заполненное событие и сохраняет в БД.
     * Может вызываться из HTTP-контроллера, Kafka-слушателя, тестового генератора и т.д.
     */
    @Transactional
    public void onSensorMeasured(SensorMeasuredEvent event) {
        TelemetryDataEntity telemetryData = new TelemetryDataEntity();
        telemetryData.setSensorId(event.sensorId());
        telemetryData.setSensorValue(event.value());
        telemetryData.setMetricCode(event.metricCode());
        telemetryData.setMeasuredAt(event.measuredAt().atOffset(ZoneOffset.UTC));
        telemetryData.setReceivedAt(OffsetDateTime.now());
        telemetryData.setValueJson(getJsonValue(event));

        telemetryRepository.save(telemetryData);
    }

    private static Map<String, Object> getJsonValue(SensorMeasuredEvent event) {
        Map<String, Object> json = new HashMap<>();
        json.put("value", event.value());
        if (event.unit() != null && !event.unit().isBlank()) {
            json.put("unit", event.unit());
        }
        return json;
    }
}
