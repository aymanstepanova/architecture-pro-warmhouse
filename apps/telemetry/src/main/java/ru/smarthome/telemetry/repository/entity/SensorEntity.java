package ru.smarthome.telemetry.repository.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "sensors", schema = "telemetry")
@Getter
@Setter
public class SensorEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;


    @Column(name = "location")
    private String location;

    @Column(name = "sensor_id", nullable = false, unique = true)
    private String externalSensorId;

    @Column(name = "sensor_type", nullable = false)
    private String sensorType;

    @Column(name = "unit")
    private String unit;
    @Column(name = "status")
    private String status;
    @Column(name = "sensor_name")
    private String sensorName;
    @Column(name = "house_id")
    private UUID houseId;

}
