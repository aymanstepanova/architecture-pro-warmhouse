package ru.smarthome.telemetry.model;

public enum MetricCode {

    TEMPERATURE("temperature"),
    HUMIDITY("humidity");
    final String code;

    MetricCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
