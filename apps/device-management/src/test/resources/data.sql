INSERT INTO device_mgmt.devices(id, device_key, type_code, model, location, status, metadata)
VALUES
 ('00000000-0000-0000-0000-0000000000a1','dev-thermo-1','thermostat','t-100','Living Room','active','{"brand":"Acme"}'::jsonb),
 ('00000000-0000-0000-0000-0000000000b2','dev-sensor-1','sensor','hum-1','Kitchen','active','{"brand":"Beta"}'::jsonb);
