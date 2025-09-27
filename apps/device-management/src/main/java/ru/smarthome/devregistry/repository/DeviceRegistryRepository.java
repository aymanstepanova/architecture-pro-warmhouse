package ru.smarthome.devregistry.repository;


import ru.smarthome.devregistry.model.Device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRegistryRepository {
    Device save(Device device);
    Optional<Device> findById(UUID id);
    List<Device> find(UUID homeId, String typeCode, String location);
    boolean existsByDeviceKey(String deviceKey);
}

