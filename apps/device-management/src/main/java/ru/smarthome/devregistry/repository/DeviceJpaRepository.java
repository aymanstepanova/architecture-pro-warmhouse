package ru.smarthome.devregistry.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.smarthome.devregistry.repository.entity.DeviceEntity;

import java.util.Optional;
import java.util.UUID;

public interface DeviceJpaRepository extends JpaRepository<DeviceEntity, UUID> {
    Optional<DeviceEntity> findByDeviceKey(String deviceKey);
    boolean existsByDeviceKey(String deviceKey);
}

