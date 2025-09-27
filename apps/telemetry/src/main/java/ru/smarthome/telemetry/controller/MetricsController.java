package ru.smarthome.telemetry.controller;


import lombok.RequiredArgsConstructor;
import ru.smarthome.telemetry.model.MetricDto;
import ru.smarthome.telemetry.service.MetricsQueryService;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsQueryService service;

    // Базовый сценарий:
    // GET /metrics?house_id=uuid&location=Living Room&metric_code=humidity
    @GetMapping
    public List<MetricDto> byLocationAndMetric(
            @RequestParam(name = "house_id") @Size(max = 128) String houseId,
            @RequestParam(name = "location", required = false) @Size(max = 128) String location,
            @RequestParam(name = "metric_code", required = false) @Size(max = 64) String metricCode) {
        return service.findLatestByLocationAndMetric(houseId, location, metricCode);
    }

    // Вторичный сценарий:
    // GET /metrics/{sensor_id}
    // GET /metrics/{sensor_id}?useExternalId=true
    @GetMapping("/{sensor_id}")
    public ResponseEntity<MetricDto> bySensor(
            @PathVariable(name = "sensor_id") String sensorId,
            @RequestParam(name = "useExternalId", defaultValue = "false") boolean useExternalId) {
        return service.findLatestBySensor(sensorId, useExternalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
