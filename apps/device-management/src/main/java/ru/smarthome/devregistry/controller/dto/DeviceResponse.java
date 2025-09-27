package ru.smarthome.devregistry.controller.dto;

import java.util.Map;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String deviceKey,
        String typeCode,
        String model,
        String location,
        String status,
        Map<String,Object> metadata,
        UUID homeId,
        UUID ownerAccount
) {}