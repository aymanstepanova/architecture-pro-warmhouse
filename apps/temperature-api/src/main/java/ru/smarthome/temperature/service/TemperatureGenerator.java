package ru.smarthome.temperature.service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TemperatureGenerator {
    private final Random rnd = new Random();

    public double getTemperature(String salt) {
        // Базовая температура: 10..30
        double base = 10 + rnd.nextDouble() * 20;

        // Bias: небольшое смещение от комнаты (-2..+2)
        double bias = Math.tanh(salt.hashCode() / 5000.0) * 2;

        // Небольшой шум от датчика (-0.5..+0.5)
        double noise = (rnd.nextDouble() - 0.5);

        double value = base + bias + noise;

        // Округляем до 0.1
        return Math.round(value * 10.0) / 10.0;
    }
}
