package ru.smarthome.telemetry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.smarthome.telemetry.model.SensorCreateRequest;
import ru.smarthome.telemetry.model.SensorResponse;
import ru.smarthome.telemetry.service.SensorCommandService;

/**
 * Временно синхронное создание датчиков
 * TODO: вместо контроллера принимать событие DeviceRegistered, SensorRegistered
 */
@RestController
@RequestMapping("/sensors")
@RequiredArgsConstructor
public class SensorsController {
    private final SensorCommandService service;

    @PostMapping
    public ResponseEntity<SensorResponse> createOrUpdate(@RequestBody @Valid
                                                         SensorCreateRequest request) {
        return ResponseEntity.ok(service.createOrUpdate(request));
    }
}
