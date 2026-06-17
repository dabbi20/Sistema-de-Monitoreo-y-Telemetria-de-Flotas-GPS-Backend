# Fleet Telemetry System

Sistema de telemetría vehicular desarrollado con Spring Boot para el procesamiento de posiciones GPS, monitoreo de estados de vehículos, generación de eventos de auditoría y consulta de métricas operativas.

---

## Descripción

Fleet Telemetry System permite recibir información GPS de vehículos, calcular automáticamente su estado operativo y almacenar un historial de posiciones.

El sistema identifica tres estados principales:

* **EN_MOVIMIENTO:** el vehículo cambia su posición.
* **DETENIDO:** el vehículo permanece sin cambios de posición durante más de 60 segundos.
* **SIN_SENAL:** el vehículo no reporta información durante más de 120 segundos.

Además, registra eventos de auditoría para cada cambio importante y expone métricas globales del sistema.

---

## Tecnologías Utilizadas

* Java 17
* Spring Boot 3
* Spring Web
* Spring Data JPA
* H2 Database
* Lombok
* Maven
* JUnit 5
* Mockito
* Docker
* Docker Compose
* Postman
* UML

---

## Arquitectura

La aplicación sigue una arquitectura en capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Componentes Principales

* VehicleController
* VehicleService
* VehicleStatusService
* TelemetryEventController
* TelemetryEventService
* MetricsController
* MetricsService

---

## Modelo de Dominio

![Modelo de Dominio](docs/domain-model.png)

---

## Diagrama de Componentes

![Diagrama de Componentes](docs/components-diagram.png)

---

## Modelo de Datos

### Vehicle

Representa el estado actual de un vehículo.

Campos principales:

* vehicleId
* lastLat
* lastLng
* previousLat
* previousLng
* lastSeen
* status

---

### GpsRecord

Almacena el historial de posiciones GPS recibidas.

Campos principales:

* vehicleId
* lat
* lng
* timestamp

---

### TelemetryEvent

Registra eventos relevantes del sistema.

Tipos de evento:

* VEHICLE_CREATED
* STATUS_CHANGED
* VEHICLE_DELETED

---

## Estados del Vehículo

| Estado        | Descripción                                  |
| ------------- | -------------------------------------------- |
| EN_MOVIMIENTO | El vehículo cambió de posición               |
| DETENIDO      | Sin movimiento durante más de 60 segundos    |
| SIN_SENAL     | Sin comunicación durante más de 120 segundos |

---

## API REST

### Registrar posición GPS

```http
POST /gps
```

Ejemplo:

```json
{
  "vehicle_id": "VH-100",
  "lat": 4.7110,
  "lng": -74.0721,
  "timestamp": "2026-06-17T07:30:00Z"
}
```

---

### Obtener todos los vehículos

```http
GET /vehicles
```

---

### Obtener vehículo por ID

```http
GET /vehicles/{vehicleId}
```

Ejemplo:

```http
GET /vehicles/VH-100
```

---

### Eliminar vehículo

```http
DELETE /vehicles/{vehicleId}
```

---

### Obtener historial GPS

```http
GET /vehicles/{vehicleId}/records
```

---

### Obtener eventos

```http
GET /events
```

---

### Obtener eventos por vehículo

```http
GET /vehicles/{vehicleId}/events
```

---

### Obtener métricas

```http
GET /metrics
```

Respuesta:

```json
{
  "totalVehicles": 2,
  "movingVehicles": 0,
  "stoppedVehicles": 0,
  "noSignalVehicles": 2
}
```

---

## Manejo de Errores

### Vehículo inexistente

```http
GET /vehicles/VH-999
```

Respuesta:

```json
{
  "message": "Vehículo no encontrado: VH-999",
  "status": 404
}
```

---

### Coordenadas inválidas

Solicitud:

```json
{
  "vehicle_id": "VH-002",
  "lat": 200,
  "lng": -74.0721
}
```

Respuesta:

```json
{
  "message": "La latitud debe estar entre -90 y 90",
  "status": 400
}
```

---

## Evidencias de Pruebas

### Crear vehículo

![Create Vehicle](docs/postman-create-vehicle.png)

### Cambio a estado EN_MOVIMIENTO

![Vehicle Moving](docs/postman-vehicle-moving.png)

### Cambio a estado DETENIDO

![Vehicle Stopped](docs/postman-vehicle-stopped.png)

### Obtener vehículos

![Get Vehicles](docs/postman-get-vehicles.png)

### Eventos de telemetría

![Events](docs/postman-get-events.png)

### Métricas

![Metrics](docs/postman-get-metrics.png)

### Error de validación

![Validation Error](docs/postman-validation-error.png)

### Vehículo inexistente

![Vehicle Not Found](docs/postman-vehicle-not-found.png)

---

## Pruebas Unitarias

El proyecto incluye pruebas unitarias para validar la lógica principal de negocio.

### Cobertura de pruebas

#### VehicleServiceTest

Valida:

* Creación de vehículos
* Búsqueda por ID
* Eliminación de vehículos
* Consulta de vehículos existentes
* Manejo de vehículos inexistentes

#### VehicleStatusServiceTest

Valida:

* Estado EN_MOVIMIENTO
* Estado DETENIDO
* Estado SIN_SENAL
* Cambios de estado
* Reglas de transición

#### MetricsServiceTest

Valida:

* Conteo total de vehículos
* Vehículos en movimiento
* Vehículos detenidos
* Vehículos sin señal

#### FleetTelemetrySystemApplicationTests

Valida:

* Carga correcta del contexto Spring Boot

### Resultado de ejecución

```bash
mvn test
```

Resultado:

```text
Tests run: 13
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

## Dockerización

El sistema puede ejecutarse completamente mediante Docker.

### Construir imagen

```bash
docker build -t fleet-telemetry-system .
```

### Ejecutar contenedor

```bash
docker run -p 8082:8081 fleet-telemetry-system
```

Aplicación disponible en:

```text
http://localhost:8082
```

---

## Docker Compose

Levantar servicios:

```bash
docker compose up -d
```

Verificar contenedores:

```bash
docker ps
```

Detener servicios:

```bash
docker compose down
```

Aplicación disponible en:

```text
http://localhost:8082
```

---

## Ejecución Local

Clonar repositorio:

```bash
git clone https://github.com/dabbi20/Sistema-de-Monitoreo-y-Telemetria-de-Flotas-GPS-Backend.git
```

Entrar al proyecto:

```bash
cd fleet-telemetry-system
```

Compilar:

```bash
./mvnw clean install
```

Ejecutar:

```bash
./mvnw spring-boot:run
```

Aplicación disponible en:

```text
http://localhost:8081
```

---

## Estructura del Proyecto

```text
fleet-telemetry-system
│
├── docs
│   ├── components-diagram.png
│   ├── domain-model.png
│   ├── postman-create-vehicle.png
│   ├── postman-get-events.png
│   ├── postman-get-metrics.png
│   ├── postman-get-vehicles.png
│   ├── postman-validation-error.png
│   ├── postman-vehicle-moving.png
│   ├── postman-vehicle-not-found.png
│   └── postman-vehicle-stopped.png
│
├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   │
│   └── test
│       └── java
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Estado del Proyecto

Proyecto finalizado en su versión MVP.

Funcionalidades implementadas:

* Registro de posiciones GPS
* Gestión automática de estados de vehículos
* Historial de posiciones
* Eventos de auditoría
* Métricas operativas
* Validaciones de entrada
* Manejo global de excepciones
* Base de datos H2
* API REST
* Pruebas unitarias
* Docker
* Docker Compose
* Documentación UML
* Evidencias Postman

---

## Autor

### David Carrasco

Estudiante de Ingeniería de Sistemas | Desarrollador Backend Java

GitHub:
https://github.com/dabbi20
