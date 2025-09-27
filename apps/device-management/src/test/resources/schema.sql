CREATE SCHEMA IF NOT EXISTS device_mgmt;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS device_mgmt.devices (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_key     TEXT NOT NULL UNIQUE,
    house_id       UUID,
    owner_account  UUID,
    type_code      TEXT NOT NULL,
    model          TEXT,
    location       TEXT,
    status         TEXT NOT NULL DEFAULT 'active',
    metadata       JSONB,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_devices_home     ON device_mgmt.devices(house_id);
CREATE INDEX IF NOT EXISTS idx_devices_location ON device_mgmt.devices(location);
CREATE INDEX IF NOT EXISTS idx_devices_type     ON device_mgmt.devices(type_code);
CREATE INDEX IF NOT EXISTS idx_devices_status   ON device_mgmt.devices(status);


