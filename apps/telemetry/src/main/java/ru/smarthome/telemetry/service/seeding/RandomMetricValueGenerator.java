package ru.smarthome.telemetry.service.seeding;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
public class RandomMetricValueGenerator implements MetricValueGenerator {

    private final Random rnd;

    public RandomMetricValueGenerator() {
        this.rnd = new Random();
    }

    @Override
    public BigDecimal next(String metricCode) {
        double raw = switch (metricCode == null ? "" : metricCode.toLowerCase()) {
            case "temperature" -> 20.0 + rnd.nextDouble() * 6.0;   // 20..26 C
            case "humidity"    -> 35.0 + rnd.nextDouble() * 30.0;  // 35..65 %
            case "power"       -> 10.0 + rnd.nextDouble() * 90.0;  // 10..100 W
            default            -> rnd.nextDouble() * 100.0;
        };
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }
}
