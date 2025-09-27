package ru.smarthome.telemetry.controller;


import org.springframework.test.context.jdbc.Sql;
import ru.smarthome.telemetry.it.PostgresIntegrationTest;
import ru.smarthome.telemetry.model.MetricDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.smarthome.telemetry.model.MetricCode.HUMIDITY;
import static ru.smarthome.telemetry.model.MetricCode.TEMPERATURE;

@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MetricsControllerIT extends PostgresIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void getByLocationAndMetric_returnsLatestPerSensor() {
        String houseId = "00000000-0000-0000-0000-000000000001"; // дом
        String url = "/metrics?location=Living Room&metric_code=humidity&house_id=%s".formatted(houseId);
        ResponseEntity<MetricDto[]> response = rest.getForEntity(url, MetricDto[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MetricDto[] body = response.getBody();
        assertThat(body).isNotNull();
        // В таблице один humidity-сенсор → один элемент
        assertThat(body.length).isEqualTo(1);
        MetricDto metricDto = body[0];

        assertThat(metricDto.sensorType()).isEqualTo(HUMIDITY.getCode());
        assertThat(metricDto.location()).isEqualTo("Living Room");
        assertThat(metricDto.metricCode()).isEqualTo(HUMIDITY.getCode());
        assertThat(metricDto.value()).isEqualByComparingTo(new BigDecimal("47.5")); // последнее значение
        assertThat(metricDto.externalSensorId()).isEqualTo("ext-hum-1");
    }

    @Test
    void getBySensor_withInternalUuid_returnsLatest() {
        // sensor UUID из data.sql
        String uuid = "00000000-0000-0000-0000-000000000002"; // temperature
        ResponseEntity<MetricDto> response = rest.getForEntity("/metrics/{id}", MetricDto.class, uuid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MetricDto metricDto = response.getBody();
        assertThat(metricDto).isNotNull();
        assertThat(metricDto.metricCode()).isEqualTo(TEMPERATURE.getCode());
        assertThat(metricDto.value()).isEqualByComparingTo(new BigDecimal("23.0"));
        assertThat(metricDto.externalSensorId()).isEqualTo("ext-temp-1");
    }

    @Test
    void getBySensor_withExternalIdFlag_returnsLatest() {
        // передаём внешний sensor_id и флаг useExternalId=true
        ResponseEntity<MetricDto> resp = rest.getForEntity(
                "/metrics/{id}?useExternalId=true", MetricDto.class, "ext-hum-1");

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        MetricDto metricDto = resp.getBody();
        assertThat(metricDto).isNotNull();
        assertThat(metricDto.metricCode()).isEqualTo(HUMIDITY.getCode());
        assertThat(metricDto.value()).isEqualByComparingTo(new BigDecimal("47.5"));
    }


    @Test
    void badRequest() {
        String url = "/metrics?location=Living Room&metric_code=humidity&house_id=%s".formatted(1);
        ResponseEntity<ProblemDetail> response = rest.getForEntity(url, ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
