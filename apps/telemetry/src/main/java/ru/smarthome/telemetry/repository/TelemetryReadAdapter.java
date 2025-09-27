package ru.smarthome.telemetry.repository;


import ru.smarthome.telemetry.model.MetricDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TelemetryReadAdapter implements TelemetryReadRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public TelemetryReadAdapter(NamedParameterJdbcTemplate jdbcTemplate, 
                                ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<MetricDto> findLatestByLocationAndMetric(String houseId,
                                                         String location,
                                                         String metricCode) {
        String sql = 
            """
            WITH filtered_sensors AS (
              SELECT id, sensor_id, sensor_type, unit, location
              FROM telemetry.sensors
              WHERE (:location IS NULL OR location = :location) and house_id = :houseId
            ),
            last_points AS (
              SELECT td.sensor_id, td.sensor_value, td.value_json, td.metric_code, td.measured_at,
                     ROW_NUMBER() OVER (PARTITION BY td.sensor_id ORDER BY td.measured_at DESC) rn
              FROM telemetry.telemetry_data td
              JOIN filtered_sensors s ON s.id = td.sensor_id
              WHERE (:metricCode IS NULL OR td.metric_code = :metricCode)
            )
            SELECT s.sensor_id AS external_sensor_id, s.sensor_type, s.unit, s.location,
                   lp.metric_code, lp.sensor_value, lp.value_json, lp.measured_at
            FROM last_points lp
            JOIN telemetry.sensors s ON s.id = lp.sensor_id
            WHERE lp.rn = 1
            ORDER BY s.location, s.sensor_type
            """;

        var params = new MapSqlParameterSource()
                .addValue("location", location)
                .addValue("houseId", UUID.fromString(houseId))
                .addValue("metricCode", metricCode);

        return jdbcTemplate.query(sql, params, (rs, rn) -> new MetricDto(
                rs.getString("external_sensor_id"),
                rs.getString("sensor_type"),
                rs.getString("unit"),
                rs.getString("location"),
                rs.getString("metric_code"),
                rs.getBigDecimal("sensor_value"),
                readJsonMap(rs.getString("value_json")),
                rs.getTimestamp("measured_at").toInstant()
        ));
    }

    @Override
    public Optional<MetricDto> findLatestBySensorByInternalId(String sensorUuid) {
        String sql =
            """
            SELECT s.sensor_id AS external_sensor_id, s.sensor_type, s.unit, s.location,
                   td.metric_code, td.sensor_value, td.value_json, td.measured_at
            FROM telemetry.telemetry_data td
            JOIN telemetry.sensors s ON s.id = td.sensor_id
            WHERE td.sensor_id = CAST(:sensorUuid AS uuid)
            ORDER BY td.measured_at DESC
            LIMIT 1
            """;
        var params = new MapSqlParameterSource().addValue("sensorUuid", sensorUuid);
        var list = jdbcTemplate.query(sql, params, (rs, rn) -> new MetricDto(
                rs.getString("external_sensor_id"),
                rs.getString("sensor_type"),
                rs.getString("unit"),
                rs.getString("location"),
                rs.getString("metric_code"),
                rs.getBigDecimal("sensor_value"),
                readJsonMap(rs.getString("value_json")),
                rs.getTimestamp("measured_at").toInstant()
        ));
        return list.stream().findFirst();
    }

    @Override
    public Optional<MetricDto> findLatestBySensorByExternalId(String externalSensorId) {
        String sql =
            """
            SELECT s.sensor_id AS external_sensor_id, s.sensor_type, s.unit, s.location,
                   td.metric_code, td.sensor_value, td.value_json, td.measured_at
            FROM telemetry.telemetry_data td
            JOIN telemetry.sensors s ON s.id = td.sensor_id
            WHERE s.sensor_id = :externalSensorId
            ORDER BY td.measured_at DESC
            LIMIT 1
            """;
        var params = new MapSqlParameterSource().addValue("externalSensorId", externalSensorId);
        var list = jdbcTemplate.query(sql, params, (rs, rn) -> new MetricDto(
                rs.getString("external_sensor_id"),
                rs.getString("sensor_type"),
                rs.getString("unit"),
                rs.getString("location"),
                rs.getString("metric_code"),
                rs.getBigDecimal("sensor_value"),
                readJsonMap(rs.getString("value_json")),
                rs.getTimestamp("measured_at").toInstant()
        ));
        return list.stream().findFirst();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonMap(String json) {
        try {
            return json == null ? null : objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Map.of(); // не валим запрос, отдаём пустую мапу
        }
    }
}

