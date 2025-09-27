#!/bin/bash

echo "=== Тестирование API Gateway для device-management ==="

# Ждем запуска сервисов
echo "Ожидание запуска сервисов..."
sleep 30

# Проверяем health check
echo "1. Проверка health check smart-home..."
curl -s http://localhost:8080/health | jq .


# Тестируем создание устройства через API Gateway
echo -e "\n3. Создание устройства через API Gateway (POST /api/v1/devices)..."
curl -X POST http://localhost:8080/api/v1/devices \
  -H "Content-Type: application/json" \
  -d '{
    "deviceKey": "test-device-001",
    "typeCode": "thermostat",
    "model": "Smart Thermostat Pro",
    "location": "Living Room",
    "status": "active",
    "metadata": {
      "firmware_version": "1.2.3",
      "serial_number": "STP-001-2024"
    },
    "homeId": "550e8400-e29b-41d4-a716-446655440000",
    "ownerAccount": "550e8400-e29b-41d4-a716-446655440001"
  }' | jq .

# Тестируем получение списка устройств через API Gateway
echo -e "\n4. Получение списка устройств через API Gateway (GET /api/v1/devices)..."
curl -s http://localhost:8080/api/v1/devices | jq .

# Тестируем получение конкретного устройства (если ID известен)
echo -e "\n5. Получение конкретного устройства через API Gateway..."
# Сначала получим ID из списка
DEVICE_ID=$(curl -s http://localhost:8080/api/v1/devices | jq -r '.[0].id // empty')
if [ ! -z "$DEVICE_ID" ]; then
    echo "Получение устройства с ID: $DEVICE_ID"
    curl -s http://localhost:8080/api/v1/devices/$DEVICE_ID | jq .
else
    echo "Устройства не найдены"
fi

echo -e "\n=== Тестирование завершено ==="
