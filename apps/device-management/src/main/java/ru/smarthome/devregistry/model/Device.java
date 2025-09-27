package ru.smarthome.devregistry.model;

import java.util.Map;
import java.util.UUID;

public record Device(
        UUID id,
        String deviceKey,
        String typeCode,
        String model,
        String location,
        String status,
        Map<String,Object> metadata,
        UUID houseId,
        UUID ownerAccount
) {
    public static Device create(String deviceKey, String typeCode, String model, String location,
                                String status, Map<String,Object> metadata, UUID homeId, UUID ownerAccount) {
        return new Device(null, deviceKey, typeCode, model, location, status, metadata, homeId, ownerAccount);
    }
    public Device withId(UUID id) { return new Device(id, deviceKey, typeCode, model, location, status, metadata, houseId, ownerAccount); }
}

