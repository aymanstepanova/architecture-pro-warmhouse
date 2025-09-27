-- два сенсора "в спальне"
INSERT INTO telemetry.sensors (id, sensor_id, sensor_type, unit, location)
VALUES
  ('00000000-0000-0000-0000-000000000001', 'ext-hum-1', 'humidity', '%', 'Living Room'),
  ('00000000-0000-0000-0000-000000000002', 'ext-temp-1', 'temperature', 'C', 'Living Room');

-- отметим по два измерения на каждый, у humidity последнее новее
INSERT INTO telemetry.telemetry_data (sensor_id, sensor_value, metric_code, value_json, measured_at, received_at)
VALUES
  ('00000000-0000-0000-0000-000000000001', 45.0, 'humidity',  '{"value":45,"unit":"%"}',  '2025-09-26T10:00:00Z', '2025-09-26T10:00:01Z'),
  ('00000000-0000-0000-0000-000000000001', 47.5, 'humidity',  '{"value":47.5,"unit":"%"}','2025-09-26T12:00:00Z', '2025-09-26T12:00:01Z'),

  ('00000000-0000-0000-0000-000000000002', 22.1, 'temperature','{"value":22.1,"unit":"C"}','2025-09-26T09:55:00Z', '2025-09-26T09:55:01Z'),
  ('00000000-0000-0000-0000-000000000002', 23.0, 'temperature','{"value":23.0,"unit":"C"}','2025-09-26T10:30:00Z', '2025-09-26T10:30:01Z');
