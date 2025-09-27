# Smart Home API Gateway

Smart Home приложение теперь работает как API Gateway, объединяющий различные микросервисы в единый API.

## Архитектура

```
Client -> Smart Home (API Gateway) -> Device Management Service
                                -> Telemetry Service  
                                -> Temperature API
```

## Prerequisites

- Docker and Docker Compose

## Getting Started

### Option 1: Using Docker Compose (Recommended)

The easiest way to start the application is to use Docker Compose:

```bash
./init.sh
```

This script will:

1. Build and start all services (PostgreSQL, Temperature API, Telemetry Service, Device Management Service, and API Gateway)
2. Wait for the services to be ready
3. Display information about how to access the API

Alternatively, you can run Docker Compose directly:

```bash
docker-compose up -d
```

The API Gateway will be available at http://localhost:8080

### Option 2: Manual setup

If you prefer to run the application without Docker:

1. Start all required services:

```bash
docker-compose up -d postgres telemetry-postgres device-postgres temperature-api telemetry-service device-management-service
```

2. Build and run the API Gateway:

```bash
go build -o smarthome
./smarthome
```

## API Testing

A Postman collection is provided for testing the API. Import the `smarthome-api.postman_collection.json` file into Postman to get started.

You can also use the provided test script:

```bash
./test_api_gateway.sh
```

## API Endpoints

### Device Management (через API Gateway)

- `POST /api/v1/devices` - Register a new device
- `GET /api/v1/devices` - Get all devices (with optional filters)
- `GET /api/v1/devices/:id` - Get a specific device

### Sensors (локальные API)

- `GET /health` - Health check
- `GET /api/v1/sensors` - Get all sensors
- `GET /api/v1/sensors/:id` - Get a specific sensor
- `POST /api/v1/sensors` - Create a new sensor
- `PUT /api/v1/sensors/:id` - Update a sensor
- `DELETE /api/v1/sensors/:id` - Delete a sensor
- `PATCH /api/v1/sensors/:id/value` - Update a sensor's value and status
- `GET /api/v1/sensors/temperature/:location` - Get temperature by location

## Services

- **API Gateway** (port 8080) - Smart Home приложение, работающее как API Gateway
- **Device Management** (port 8083) - Управление устройствами
- **Telemetry Service** (port 8082) - Сбор и хранение телеметрии
- **Temperature API** (port 8081) - API для работы с температурными данными

## Documentation

Подробная документация по API Gateway доступна в [API_GATEWAY_README.md](API_GATEWAY_README.md)
