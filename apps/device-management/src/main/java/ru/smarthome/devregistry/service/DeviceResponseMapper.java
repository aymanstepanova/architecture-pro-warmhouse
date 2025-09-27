package ru.smarthome.devregistry.service;

import ru.smarthome.devregistry.controller.dto.DeviceResponse;
import ru.smarthome.devregistry.model.Device;

final class DeviceResponseMapper {
    static DeviceResponse fromDomain(Device device) {
        return new DeviceResponse(
                device.id(), device.deviceKey(), device.typeCode(), device.model(),
                device.location(), device.status(), device.metadata(), device.houseId(), device.ownerAccount()
        );
    }
}
