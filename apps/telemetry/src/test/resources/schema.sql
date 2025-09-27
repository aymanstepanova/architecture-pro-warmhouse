CREATE SCHEMA IF NOT EXISTS telemetry;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- справочник сенсоров
CREATE TABLE IF NOT EXISTS telemetry.sensors (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    house_id        UUID NOT NULL, -- внешний ID дома (таблица houses в БД SpacesService)
    sensor_id       TEXT NOT NULL UNIQUE,
    device_id       TEXT,
    sensor_type     TEXT NOT NULL,
    unit            TEXT NOT NULL,
    location        TEXT,
    status          TEXT NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- временной ряд
CREATE TABLE IF NOT EXISTS telemetry.telemetry_data (
    id              BIGSERIAL,
    sensor_id       UUID NOT NULL REFERENCES telemetry.sensors(id) ON DELETE CASCADE,
    sensor_value    NUMERIC(18,6) NOT NULL,
    metric_code     TEXT NOT NULL,
    value_json      JSONB NOT NULL,
    measured_at     TIMESTAMPTZ NOT NULL,
    received_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT telemetry_data_pk PRIMARY KEY (sensor_id, measured_at)
) PARTITION BY RANGE (measured_at);

CREATE INDEX IF NOT EXISTS idx_sensors_location    ON telemetry.sensors(location);
CREATE INDEX IF NOT EXISTS idx_sensors_type        ON telemetry.sensors(sensor_type);
CREATE INDEX IF NOT EXISTS idx_tdata_sensor_ts_desc ON telemetry.telemetry_data(sensor_id, measured_at DESC);

-- партиции
CREATE TABLE IF NOT EXISTS telemetry.telemetry_data_2025_09
  PARTITION OF telemetry.telemetry_data
  FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');
CREATE TABLE IF NOT EXISTS telemetry.telemetry_data_2025_10
  PARTITION OF telemetry.telemetry_data
  FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');
-- локальные индексы под партиции
CREATE INDEX IF NOT EXISTS idx_tdata_2025_09_sensor_ts_desc
  ON telemetry.telemetry_data_2025_09 (sensor_id, measured_at DESC);
CREATE INDEX IF NOT EXISTS idx_tdata_2025_10_sensor_ts_desc
  ON telemetry.telemetry_data_2025_10 (sensor_id, measured_at DESC);