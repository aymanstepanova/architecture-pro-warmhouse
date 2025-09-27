package ru.smarthome.devregistry.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import ru.smarthome.devregistry.it.PostgresIntegrationTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DeviceRegistryControllerIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void create_returns200_and_persists() {
        var req = Map.of(
                "deviceKey", "dev-switch-1",
                "typeCode", "switch",
                "model", "sw-200",
                "location", "Bedroom",
                "status", "active",
                "metadata", Map.of("brand","Acme")
        );

        ResponseEntity<Map> resp = rest.postForEntity("/devices", req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<?,?> body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("deviceKey")).isEqualTo("dev-switch-1");
        assertThat(body.get("typeCode")).isEqualTo("switch");
        assertThat(body.get("location")).isEqualTo("Bedroom");
        assertThat(body.get("id")).isNotNull();

        // Сходить GET /devices/{id}
        String id = body.get("id").toString();
        ResponseEntity<Map> byId = rest.getForEntity("/devices/{id}", Map.class, id);
        assertThat(byId.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byId.getBody()).isNotNull();
        assertThat(byId.getBody().get("deviceKey")).isEqualTo("dev-switch-1");
    }

    @Test
    void get_existing_returns200() {
        // seed из data.sql
        String id = "00000000-0000-0000-0000-0000000000a1";
        ResponseEntity<Map> resp = rest.getForEntity("/devices/{id}", Map.class, id);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().get("deviceKey")).isEqualTo("dev-thermo-1");
    }

    @Test
    void list_with_filters_returnsOnlyMatched() {
        // В фикстурах есть Living Room (thermostat) и Kitchen (sensor)
        ResponseEntity<Map[]> resp = rest.getForEntity("/devices?location=Kitchen&typeCode=sensor", Map[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map[] body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.length).isEqualTo(1);
        assertThat(body[0].get("deviceKey")).isEqualTo("dev-sensor-1");
    }
}
