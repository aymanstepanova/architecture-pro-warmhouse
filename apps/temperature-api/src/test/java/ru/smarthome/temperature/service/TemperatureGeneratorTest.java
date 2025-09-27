package ru.smarthome.temperature.service;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TemperatureGeneratorTest {
    TemperatureGenerator generator = new TemperatureGenerator();

    final double MIN_TEMP = 8.0;
    final double MAX_TEMP = 32.0;

    @Test
    void temperatureWithinExpectedRange() {
        IntStream.range(0, 50).forEach(i -> {
            double t = generator.getTemperature("LivingRoom");
            assertThat(t)
                    .as("Run %d produced %f".formatted(i, t))
                    .isBetween(MIN_TEMP, MAX_TEMP);
        });
    }

    @Test
    void multipleCallsProduceDifferentValues() {
        IntStream.range(0, 50).forEach(i -> {
            double t1 = generator.getTemperature("Bedroom");
            double t2 = generator.getTemperature("Bedroom");

            assertThat(t1).isNotEqualTo(t2);
        });
    }

    @Test
    void differentSaltsShiftValues() {
        IntStream.range(0, 50).forEach(i -> {
            double t1 = generator.getTemperature("Kitchen");
            double t2 = generator.getTemperature("LivingRoom");

            assertThat(t1).isBetween(MIN_TEMP, MAX_TEMP);
            assertThat(t2).isBetween(MIN_TEMP, MAX_TEMP);
        });
    }
}
