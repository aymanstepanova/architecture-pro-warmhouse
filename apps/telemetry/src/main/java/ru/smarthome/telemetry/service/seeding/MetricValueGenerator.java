package ru.smarthome.telemetry.service.seeding;

import java.math.BigDecimal;

public interface MetricValueGenerator {
    BigDecimal next(String metricCode);
}
