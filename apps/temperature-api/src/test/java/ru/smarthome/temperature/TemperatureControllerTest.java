package ru.smarthome.temperature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.smarthome.temperature.controller.TemperatureController;
import ru.smarthome.temperature.service.TemperatureService;
import ru.smarthome.temperature.model.TemperatureReading;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TemperatureController.class)
class TemperatureControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TemperatureService service;

    @Test
    void ok_whenLocation() throws Exception {
        when(service.read("Moscow"))
                .thenReturn(new TemperatureReading("sensor-1","Moscow",21.5,"C", OffsetDateTime.parse("2025-01-01T10:00:00Z")));

        mvc.perform(get("/temperature").param("location", "Moscow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Moscow"))
                .andExpect(jsonPath("$.unit").value("C"));
    }

    @Test
    void badRequest_whenNoLocation() throws Exception {
        mvc.perform(get("/temperature"))
                .andExpect(status().isBadRequest());
    }
}
