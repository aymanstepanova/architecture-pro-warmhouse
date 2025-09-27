package ru.smarthome.devregistry.controller;

import ru.smarthome.devregistry.controller.dto.DeviceCreateRequest;
import ru.smarthome.devregistry.controller.dto.DeviceResponse;
import ru.smarthome.devregistry.service.DeviceRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
public class DeviceRegistryController {

    private final DeviceRegistryService service;

    public DeviceRegistryController(DeviceRegistryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@RequestBody @Valid DeviceCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public List<DeviceResponse> list(
            @RequestParam(name = "houseId", required = false) UUID houseId,
            @RequestParam(name = "typeCode", required = false) String typeCode,
            @RequestParam(name = "location", required = false) String location) {
        return service.find(houseId, typeCode, location);
    }

    @GetMapping("/{device_id}")
    public ResponseEntity<DeviceResponse> get(
            @PathVariable(name = "device_id") String deviceId) {
        return service.get(UUID.fromString(deviceId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }
}

