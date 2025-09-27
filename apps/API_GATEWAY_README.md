# API Gateway для Smart Home

Smart Home приложение теперь работает как API Gateway, проксируя запросы к различным микросервисам.

## Архитектура

```
Client -> Smart Home (API Gateway) -> Device Management Service
                                   -> Telemetry Service  
                                   -> Temperature API (deprecated)
```

## Доступные API

### Device Management (через API Gateway)

#### POST /api/v1/devices
Регистрация нового устройства

**Запрос:**
```json
{
  "deviceKey": "string (required)",
  "typeCode": "string (required)", 
  "model": "string (optional)",
  "location": "string (optional)",
  "status": "string (optional)",
  "metadata": "object (optional)",
  "homeId": "uuid (optional)",
  "ownerAccount": "uuid (optional)"
}
```

**Ответ:**
```json
{
  "id": "uuid",
  "deviceKey": "string",
  "typeCode": "string",
  "model": "string",
  "location": "string", 
  "status": "string",
  "metadata": "object",
  "homeId": "uuid",
  "ownerAccount": "uuid"
}
```

#### GET /api/v1/devices
Получение списка устройств

**Параметры запроса:**
- `homeId` (optional) - фильтр по ID дома
- `typeCode` (optional) - фильтр по типу устройства
- `location` (optional) - фильтр по локации

#### GET /api/v1/devices/{id}
Получение конкретного устройства по ID

### Sensors (локальные API)

#### GET /api/v1/sensors
Получение списка датчиков

#### POST /api/v1/sensors
Создание нового датчика

#### GET /api/v1/sensors/{id}
Получение датчика по ID

#### PUT /api/v1/sensors/{id}
Обновление датчика

#### DELETE /api/v1/sensors/{id}
Удаление датчика

#### PATCH /api/v1/sensors/{id}/value
Обновление значения датчика

#### GET /api/v1/sensors/temperature/{location}
Получение температуры по локации

## Переменные окружения

- `DATABASE_URL` - URL базы данных PostgreSQL
- `TEMPERATURE_API_URL` - URL сервиса температуры (по умолчанию: http://temperature-api:8081)
- `TELEMETRY_API_URL` - URL сервиса телеметрии (по умолчанию: http://telemetry-service:8082)
- `DEVICE_MANAGEMENT_API_URL` - URL сервиса управления устройствами (по умолчанию: http://device-management-service:8083)

## Запуск

```bash
# Запуск всех сервисов
docker-compose up --build -d

# Проверка статуса
docker-compose ps

# Просмотр логов
docker-compose logs -f app
```

## Тестирование

```bash
# Запуск тестового скрипта
./test_api_gateway.sh
```

## Примеры использования

### Создание устройства через API Gateway

```bash
curl -X POST http://localhost:8080/api/v1/devices \
  -H "Content-Type: application/json" \
  -d '{
    "deviceKey": "thermostat-001",
    "typeCode": "thermostat",
    "model": "Smart Thermostat Pro",
    "location": "Living Room",
    "status": "active",
    "metadata": {
      "firmware_version": "1.2.3"
    }
  }'
```

### Получение списка устройств

```bash
curl http://localhost:8080/api/v1/devices
```

### Получение устройств по фильтру

```bash
curl "http://localhost:8080/api/v1/devices?typeCode=thermostat&location=Living%20Room"
```
