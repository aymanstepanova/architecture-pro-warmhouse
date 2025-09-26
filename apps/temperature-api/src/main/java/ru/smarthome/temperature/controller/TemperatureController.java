package ru.smarthome.temperature.controller;

import ru.smarthome.temperature.model.TemperatureReading;
import ru.smarthome.temperature.service.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class TemperatureController {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureController.class);
    private final TemperatureService service;

    public TemperatureController(TemperatureService service) {
        this.service = service;
    }

    @GetMapping("/temperature")
    public TemperatureReading getTemperature(@RequestParam(name = "location", required = false)
                                             String location) {
        logger.info(location);
        return service.read(location);
    }

    @GetMapping("/temperature/{sensorId}")
    public TemperatureReading getBySensor(@PathVariable("sensorId") String sensorId) {
        return service.read(sensorId);
    }
}
