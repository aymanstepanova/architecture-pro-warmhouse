package ru.smarthome.devregistry.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import java.util.Map;

public record DeviceCreateRequest(
        @NotBlank @Size(max=100) String deviceKey,
        @NotBlank @Size(max=50) String typeCode,
        @Size(max=100) String model,
        @Size(max=100) String location,
        @Size(max=30) String status,
        Map<String,Object> metadata,
        UUID homeId,
        UUID ownerAccount
) {}

