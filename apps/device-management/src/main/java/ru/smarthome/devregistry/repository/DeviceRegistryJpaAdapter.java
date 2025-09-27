package ru.smarthome.devregistry.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.smarthome.devregistry.model.Device;
import ru.smarthome.devregistry.repository.entity.DeviceEntity;

import java.util.*;

@Component
public class DeviceRegistryJpaAdapter implements DeviceRegistryRepository {

    private final DeviceJpaRepository repository;
    private final ObjectMapper objectMapper;

    public DeviceRegistryJpaAdapter(DeviceJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Device save(Device d) {
        DeviceEntity e = toEntity(d);
        if (e.getId() == null) e.setId(UUID.randomUUID());
        e = repository.save(e);
        return toDomain(e);
    }

    @Override
    public Optional<Device> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Device> find(UUID homeId, String typeCode, String location) {
        // MVP: простая фильтрация в памяти (для десятков записей). Для продакшн — спецификации/NativeQuery.
        var all = repository.findAll();
        return all.stream().map(this::toDomain)
                .filter(d -> homeId == null || homeId.equals(d.houseId()))
                .filter(d -> typeCode == null || typeCode.equals(d.typeCode()))
                .filter(d -> location == null || location.equals(d.location()))
                .toList();
    }

    @Override
    public boolean existsByDeviceKey(String deviceKey) {
        return repository.existsByDeviceKey(deviceKey);
    }

    private Device toDomain(DeviceEntity e) {
        Map<String, Object> meta = null;
        try {
            meta = e.getMetadata() == null ? null : e.getMetadata();
        } catch (Exception ignore) {
        }
        return new Device(e.getId(), e.getDeviceKey(), e.getTypeCode(), e.getModel(),
                e.getLocation(), e.getStatus(), meta, e.getHouseId(), e.getOwnerAccount());
    }

    private DeviceEntity toEntity(Device d) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(d.id());
        deviceEntity.setDeviceKey(d.deviceKey());
        deviceEntity.setTypeCode(d.typeCode());
        deviceEntity.setModel(d.model());
        deviceEntity.setLocation(d.location());
        deviceEntity.setStatus(d.status() == null ? "active" : d.status());
        try {
            Map<String, Object> metadata = d.metadata() == null
                    ? null
                    : d.metadata();
            deviceEntity.setMetadata(metadata);
        } catch (Exception ignore) {
        }
        deviceEntity.setHouseId(d.houseId());
        deviceEntity.setOwnerAccount(d.ownerAccount());
        return deviceEntity;
    }
}
