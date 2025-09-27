-- =========================================================
-- Telemetry Service Database (MVP)
-- Схема: telemetry
-- Таблицы: sensors, telemetry_data
-- =========================================================

-- 1) Сервисная подготовка
CREATE SCHEMA IF NOT EXISTS telemetry;

-- UUID и таймзона пригодятся
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================================
-- Таблица: sensors — реестр датчиков
-- ---------------------------------------------------------
-- Назначение:
--  - хранит идентификатор датчика в системе (id),
--  - внешний идентификатор датчика (sensor_id) — то, как он известен монолиту/устройству,
--  - тип/единицы измерения, локация, статус, метаданные.
-- =========================================================
CREATE TABLE IF NOT EXISTS telemetry.sensors (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),          -- внутр. PK
    house_id        UUID NULL,                                            -- внешний ID дома (таблица houses в БД SpacesService)
    sensor_id       TEXT NOT NULL UNIQUE,                                 -- внешний ID датчика (таблица sensors в БД монолита)
    device_id       TEXT,                                                 -- внешний ID устройства (таблица devices в БД DeviceRegistry)
    sensor_type     TEXT NOT NULL,                                        -- тип датчика (например, temperature, humidity, power)
    sensor_name     TEXT NOT NULL,                                        -- имя датчика
    unit            TEXT NOT NULL,                                        -- единица измерения (например, C, %, W)
    location        TEXT,                                                 -- произвольная локация/комната/зона
    status          TEXT NOT NULL DEFAULT 'active',                       -- жизненный цикл: active|inactive|maintenance (свободный текст на MVP)
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),                   -- когда добавили запись
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()                    -- когда меняли запись
);

COMMENT ON TABLE telemetry.sensors IS
    'Реестр датчиков телеметрии. Хранит данные по каждому датчику.';

COMMENT ON COLUMN telemetry.sensors.id            IS 'Внутренний UUID первичный ключ датчика.';
COMMENT ON COLUMN telemetry.sensors.house_id      IS 'Внешний ID дома (таблица houses в БД SpacesService)';
COMMENT ON COLUMN telemetry.sensors.sensor_id     IS 'Внешний уникальный ID датчика, известный другим системам (монолит, устройство). Уникален.';
COMMENT ON COLUMN telemetry.sensors.device_id     IS 'Внешний ID связанного устройства (м.б. ссылка на devices?)';
COMMENT ON COLUMN telemetry.sensors.sensor_type   IS 'Тип датчика: temperature, humidity, power и т.д. ';
COMMENT ON COLUMN telemetry.sensors.unit          IS 'Единицы измерения значения: например, C (градусы Цельсия), %, W.';
COMMENT ON COLUMN telemetry.sensors.location      IS 'Человекочитаемая локация/комната/зона, где установлен датчик.';
COMMENT ON COLUMN telemetry.sensors.status        IS 'Статус жизненного цикла: active|inactive|maintenance.';
COMMENT ON COLUMN telemetry.sensors.created_at    IS 'Время создания записи о датчике.';
COMMENT ON COLUMN telemetry.sensors.updated_at    IS 'Время последнего обновления записи о датчике.';

-- Полезные индексы для фильтров/поиска
CREATE INDEX IF NOT EXISTS idx_sensors_sensor_type  ON telemetry.sensors (sensor_type);
CREATE INDEX IF NOT EXISTS idx_sensors_location     ON telemetry.sensors (location);
CREATE INDEX IF NOT EXISTS idx_sensors_status       ON telemetry.sensors (status);
CREATE INDEX IF NOT EXISTS idx_sensors_sensor_id    ON telemetry.sensors (sensor_id);

-- Триггер для updated_at
CREATE OR REPLACE FUNCTION telemetry.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at := NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sensors_set_updated_at ON telemetry.sensors;
CREATE TRIGGER trg_sensors_set_updated_at
BEFORE UPDATE ON telemetry.sensors
FOR EACH ROW EXECUTE FUNCTION telemetry.set_updated_at();

-- =========================================================
-- Таблица: telemetry_data — сырые показания (временные ряды)
-- ---------------------------------------------------------
-- Назначение:
--  - хранит все поступающие измерения,
--  - минимальный состав: sensor_id, measured_at, sensor_value,
--  - качество и источник для дебага/фильтрации.
-- Примечание:
--  - Для больших объёмов позже можно включить партиционирование по partition_ym (месяц/день) и удаление старых партиций по расписанию
-- =========================================================
CREATE TABLE IF NOT EXISTS telemetry.telemetry_data (
    id              BIGSERIAL,                                            -- суррогатный ключ
    sensor_id       UUID NOT NULL REFERENCES telemetry.sensors(id) ON DELETE CASCADE,
    sensor_value    NUMERIC(18,6) NOT NULL,                               -- значение измерения
    metric_code     TEXT NOT NULL,                                        -- код метрики (например, temperature).
    value_json      JSONB NOT NULL,                                       -- гибкая форма значения (JSON)
    measured_at     TIMESTAMPTZ NOT NULL,                                 -- время измерения
    received_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),                   -- когда попало в БД (может отличаться от времени измерения из-за отставания)
    CONSTRAINT telemetry_data_pk PRIMARY KEY (sensor_id, measured_at)
) PARTITION BY RANGE (measured_at);


COMMENT ON TABLE telemetry.telemetry_data IS
    'Сырые показания телеметрии (временной ряд). Для каждого датчика хранится значение на момент времени.';

COMMENT ON COLUMN telemetry.telemetry_data.sensor_id    IS 'Ссылка на датчик (telemetry.sensors.id).';
COMMENT ON COLUMN telemetry.telemetry_data.sensor_value IS 'Числовое значение измерения.';
COMMENT ON COLUMN telemetry.telemetry_data.measured_at  IS 'Временная метка измерения.';
COMMENT ON COLUMN telemetry.telemetry_data.metric_code  IS 'Код метрики (например, temperature).';
COMMENT ON COLUMN telemetry.telemetry_data.received_at  IS 'Когда запись была получена и сохранена в БД.';
COMMENT ON COLUMN telemetry.telemetry_data.value_json   IS 'Гибкая форма значения (JSON), например {"value":12.3,"unit":"C"}';

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

-- Индексы под типовые запросы
CREATE INDEX IF NOT EXISTS idx_readings_sensor_measured_at_desc ON telemetry.telemetry_data (sensor_id, measured_at DESC);
CREATE INDEX IF NOT EXISTS idx_readings_measured_at     ON telemetry.telemetry_data (measured_at);

-- =========================================================
-- VIEW: последнее значение на датчик.
-- =========================================================
CREATE OR REPLACE VIEW telemetry.v_sensor_last_value AS
SELECT r.sensor_id,
       r.sensor_value       AS last_value,
       r.measured_at         AS last_measured_at
FROM telemetry.telemetry_data r
JOIN (
    SELECT sensor_id, MAX(measured_at) AS max_measured_at
    FROM telemetry.telemetry_data
    GROUP BY sensor_id
) mx ON mx.sensor_id = r.sensor_id AND mx.max_measured_at= r.measured_at;

COMMENT ON VIEW telemetry.v_sensor_last_value IS 'Вид последних значений по каждому датчику. ';
