package ru.smarthome.telemetry.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import ru.smarthome.telemetry.it.PostgresIntegrationTest;
import ru.smarthome.telemetry.repository.SensorJpaRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SensorsControllerIT extends PostgresIntegrationTest {
    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private SensorJpaRepository repo;

    @Test
    void create_new_sensor_returns200_and_persists() {
        var req = Map.of(
                "sensorId", "ext-temp-1",
                "sensorType", "temperature",
                "name", "Bedroom Temperature",
                "unit", "C",
                "location", "Bedroom",
                "status", "active"
        );
        /**
         * {
         *     "name": "Living Room Temperature",
         *     "type": "temperature",
         *     "location": "Living Room",
         *     "unit": "°C"
         * }
         */

        ResponseEntity<Map> resp = rest.postForEntity("/sensors", req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("sensorId")).isEqualTo("ext-temp-1");
        assertThat(body.get("sensorType")).isEqualTo("temperature");
        assertThat(body.get("unit")).isEqualTo("C");
        assertThat(body.get("location")).isEqualTo("Bedroom");
        assertThat(body.get("status")).isEqualTo("active");
        assertThat(body.get("id")).isNotNull();

        // Проверим, что запись действительно в БД
        var e = repo.findByExternalSensorId("ext-temp-1");
        assertThat(e).isPresent();
        assertThat(e.get().getSensorType()).isEqualTo("temperature");
        assertThat(e.get().getLocation()).isEqualTo("Bedroom");
        assertThat(e.get().getStatus()).isEqualTo("active");
        assertThat(e.get().getUnit()).isEqualTo("C");
        assertThat(e.get().getExternalSensorId()).isEqualTo("ext-temp-1");
    }
}
