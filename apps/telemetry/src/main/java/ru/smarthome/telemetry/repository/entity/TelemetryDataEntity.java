package ru.smarthome.telemetry.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "telemetry_data", schema = "telemetry")
@Getter
@Setter
public class TelemetryDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, columnDefinition = "uuid")
    private UUID sensorId;

    @Column(name = "sensor_value", nullable = false)
    private BigDecimal sensorValue;

    @Column(name = "metric_code")
    private String metricCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value_json", columnDefinition = "jsonb")
    private Map<String, Object> valueJson;

    @Column(name = "measured_at", nullable = false)
    private OffsetDateTime measuredAt;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

}
