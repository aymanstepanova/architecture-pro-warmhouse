-- Создаём отдельную БД под DeviceManagement
CREATE DATABASE device_mgmt;
\connect device_mgmt;

CREATE SCHEMA IF NOT EXISTS device_mgmt;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Таблица устройств (реестр). Это «источник истины» про принадлежность дому/аккаунту.
CREATE TABLE IF NOT EXISTS device_mgmt.devices (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),  -- внутренний PK
    device_key     TEXT NOT NULL UNIQUE,                         -- внешний стабильный ID устройства (для интеграций)
    house_id       UUID,                                         -- ID дома/хоза (MVP: мягкая ссылка)
    owner_account  UUID,                                         -- владелец (мягкая ссылка)
    type_code      TEXT NOT NULL,                                -- род устройства: thermostat, switch, sensor, hub ...
    model          TEXT,                                         -- модель/марка
    location       TEXT,                                         -- «Living Room», «Kitchen» и т.д.
    status         TEXT NOT NULL DEFAULT 'active',               -- active|inactive|maintenance
    metadata       JSONB,                                        -- доп. паспортные свойства (серийник, версии)
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE device_mgmt.devices IS
'Реестр устройств. Содержит паспортные данные, владельца и принадлежность дому/локации.';

COMMENT ON COLUMN device_mgmt.devices.device_key        IS 'Стабильный внешний ключ устройства для обращений из других сервисов.';
COMMENT ON COLUMN device_mgmt.devices.house_id          IS 'UUID дома/хозяйства (мягкая ссылка).';
COMMENT ON COLUMN device_mgmt.devices.owner_account     IS 'UUID аккаунта владельца (мягкая ссылка).';
COMMENT ON COLUMN device_mgmt.devices.type_code         IS 'Тип устройства (термостат, выключатель, сенсор и т.п.).';
COMMENT ON COLUMN device_mgmt.devices.location          IS 'Человекочитаемая локация (комната/зона).';
COMMENT ON COLUMN device_mgmt.devices.status            IS 'Статус жизненного цикла: active|inactive|maintenance.';
COMMENT ON COLUMN device_mgmt.devices.metadata          IS 'Доп. свойства устройства в формате JSON (серийник, прошивка и т.п.).';

CREATE INDEX IF NOT EXISTS idx_devices_home        ON device_mgmt.devices(house_id);
CREATE INDEX IF NOT EXISTS idx_devices_location    ON device_mgmt.devices(location);
CREATE INDEX IF NOT EXISTS idx_devices_type        ON device_mgmt.devices(type_code);
CREATE INDEX IF NOT EXISTS idx_devices_status      ON device_mgmt.devices(status);

-- триггер updated_at
CREATE OR REPLACE FUNCTION device_mgmt.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN NEW.updated_at := NOW(); RETURN NEW; END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_devices_updated_at ON device_mgmt.devices;
CREATE TRIGGER trg_devices_updated_at
BEFORE UPDATE ON device_mgmt.devices
FOR EACH ROW EXECUTE FUNCTION device_mgmt.set_updated_at();
