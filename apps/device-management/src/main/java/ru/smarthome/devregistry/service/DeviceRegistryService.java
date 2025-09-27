package ru.smarthome.devregistry.service;


import ru.smarthome.devregistry.controller.dto.DeviceCreateRequest;
import ru.smarthome.devregistry.controller.dto.DeviceResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRegistryService {
    DeviceResponse create(DeviceCreateRequest req);
    Optional<DeviceResponse> get(UUID id);
    List<DeviceResponse> find(UUID homeId, String typeCode, String location);
}

