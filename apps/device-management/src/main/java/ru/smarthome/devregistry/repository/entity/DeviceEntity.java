package ru.smarthome.devregistry.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "devices", schema = "device_mgmt")
@Setter
@Getter
public class DeviceEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "device_key", nullable = false, unique = true)
    private String deviceKey;

    @Column(name = "type_code", nullable = false)
    private String typeCode;

    private String model;
    private String location;

    @Column(nullable = false)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "house_id", columnDefinition = "uuid")
    private UUID houseId;

    @Column(name = "owner_account", columnDefinition = "uuid")
    private UUID ownerAccount;
}


