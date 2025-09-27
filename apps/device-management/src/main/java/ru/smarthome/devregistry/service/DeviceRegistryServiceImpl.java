package ru.smarthome.devregistry.service;



import org.springframework.stereotype.Service;
import ru.smarthome.devregistry.controller.dto.DeviceCreateRequest;
import ru.smarthome.devregistry.controller.dto.DeviceResponse;
import ru.smarthome.devregistry.model.Device;
import ru.smarthome.devregistry.repository.DeviceRegistryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceRegistryServiceImpl implements DeviceRegistryService {

    private final DeviceRegistryRepository repository;

    public DeviceRegistryServiceImpl(DeviceRegistryRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceResponse create(DeviceCreateRequest req) {
        if (repository.existsByDeviceKey(req.deviceKey())) {
            throw new IllegalArgumentException("device_key already exists: " + req.deviceKey());
        }
        Device d = Device.create(
                req.deviceKey(), req.typeCode(), req.model(), req.location(),
                req.status() == null ? "active" : req.status(),
                req.metadata(), req.homeId(), req.ownerAccount()
        );
        d = repository.save(d);
        return DeviceResponseMapper.fromDomain(d);
    }

    @Override
    public Optional<DeviceResponse> get(UUID id) {
        return repository.findById(id).map(DeviceResponseMapper::fromDomain);
    }

    @Override
    public List<DeviceResponse> find(UUID homeId, String typeCode, String location) {
        return repository.find(homeId, typeCode, location)
                .stream().map(DeviceResponseMapper::fromDomain).toList();
    }
}

