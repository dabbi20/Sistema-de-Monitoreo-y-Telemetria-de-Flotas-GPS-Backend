# Fleet Telemetry System - Backend

Sistema de telemetría vehicular desarrollado con Spring Boot para la recepción de coordenadas GPS, cálculo automático de estados de vehículos, generación de eventos de auditoría y consulta de métricas operativas.

---

# Descripción General

Este proyecto implementa el backend de un Sistema de Telemetría y Monitoreo de Flotas GPS.

La aplicación recibe coordenadas GPS desde múltiples vehículos, almacena el historial de posiciones y calcula automáticamente el estado operativo de cada unidad en tiempo real.

Estados soportados:

* EN_MOVIMIENTO
* DETENIDO
* SIN_SENAL

Además, el sistema registra eventos de auditoría para cambios importantes y expone métricas generales del estado de la flota.

---

# Tecnologías Utilizadas

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

---

# Arquitectura Elegida

Se implementó una arquitectura en capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Justificación

Esta arquitectura fue seleccionada porque:

* Separa responsabilidades claramente.
* Facilita el mantenimiento del código.
* Permite realizar pruebas unitarias de forma sencilla.
* Es ampliamente utilizada en aplicaciones empresariales Spring Boot.
* Facilita la escalabilidad futura del sistema.

La lógica de negocio relacionada con el cálculo de estados fue encapsulada en `VehicleStatusService`, manteniendo los controladores REST enfocados únicamente en exponer endpoints.

---

# Cumplimiento de Requisitos de la Prueba

| Requisito               | Estado |
| ----------------------- | ------ |
| POST /gps               | ✅      |
| GET /vehicles           | ✅      |
| GET /vehicles/{id}      | ✅      |
| DELETE /vehicles/{id}   | ✅      |
| Validaciones de entrada | ✅      |
| Estados automáticos     | ✅      |
| Historial GPS           | ✅      |
| Eventos de auditoría    | ✅      |
| Métricas operativas     | ✅      |
| Manejo de errores HTTP  | ✅      |
| Persistencia            | ✅      |
| Pruebas unitarias       | ✅      |
| Docker                  | ✅      |
| Docker Compose          | ✅      |

---

# Modelo de Dominio

![Modelo de Dominio](docs/domain-model.png)

---

# Diagrama de Componentes

![Diagrama de Componentes](docs/components-diagram.png)

---

# Modelo de Datos

## Vehicle

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

## GpsRecord

Representa el historial de coordenadas GPS recibidas.

Campos principales:

* vehicleId
* lat
* lng
* timestamp

---

## TelemetryEvent

Registra eventos relevantes del sistema.

Tipos soportados:

* VEHICLE_CREATED
* STATUS_CHANGED
* VEHICLE_DELETED

---

# Lógica de Estados

| Estado        | Condición                                     |
| ------------- | --------------------------------------------- |
| EN_MOVIMIENTO | Coordenadas distintas recibidas recientemente |
| DETENIDO      | Misma coordenada durante al menos 60 segundos |
| SIN_SENAL     | Sin reportes durante más de 120 segundos      |

---

# Decisión de Diseño

La especificación indica:

> "Detenido: misma coordenada sin cambio durante más de 1 minuto"

Para evitar cambios prematuros de estado, el sistema conserva el estado anterior cuando recibe coordenadas repetidas y únicamente cambia a DETENIDO después de acumular al menos 60 segundos consecutivos sin movimiento.

Esta interpretación fue documentada para mantener consistencia con la regla funcional definida en la prueba.

---

# API REST

## Registrar coordenada GPS

```http
POST /gps
```

Ejemplo:

```json
{
  "vehicle_id": "VH-001",
  "lat": 4.7110,
  "lng": -74.0721,
  "timestamp": "2026-06-17T10:00:00Z"
}
```

Respuesta:

```http
201 Created
```

---

## Obtener todos los vehículos

```http
GET /vehicles
```

---

## Obtener vehículo por ID

```http
GET /vehicles/{vehicleId}
```

---

## Eliminar vehículo

```http
DELETE /vehicles/{vehicleId}
```

---

## Historial GPS

```http
GET /vehicles/{vehicleId}/records
```

---

## Obtener eventos

```http
GET /events
```

---

## Obtener eventos por vehículo

```http
GET /vehicles/{vehicleId}/events
```

---

## Obtener métricas

```http
GET /metrics
```

---

# Manejo de Errores

## Vehículo inexistente

```json
{
  "message": "Vehículo no encontrado: VH-999",
  "status": 404
}
```

---

## Coordenadas inválidas

```json
{
  "message": "La latitud debe estar entre -90 y 90",
  "status": 400
}
```

---

# Eventos de Auditoría

El sistema registra automáticamente:

### VEHICLE_CREATED

Creación inicial de un vehículo.

### STATUS_CHANGED

Cambio de estado operativo.

### VEHICLE_DELETED

Eliminación de un vehículo.

---

# Simulador de Telemetría

La solución incluye un simulador independiente encargado de generar tráfico GPS hacia la API.

Características:

* Múltiples vehículos simulados.
* Coordenadas dentro del área de Bogotá.
* Envío periódico de posiciones.
* Vehículos en movimiento.
* Vehículos detenidos.
* Inyección de errores de validación.
* Generación automática de cambios de estado.

El simulador consume:

```http
POST /gps
```

---

# Reflexión: Eliminación de Vehículos

Si en un entorno productivo existiera simultáneamente una base de datos persistente y un sistema de caché como Redis, al eliminar un vehículo sería necesario garantizar que ambos almacenes permanezcan sincronizados.

De lo contrario, podría ocurrir que el vehículo desaparezca de la base de datos pero continúe disponible en caché, generando respuestas inconsistentes para los usuarios.

En un sistema real podrían utilizarse mecanismos como invalidación de caché, consistencia eventual o estrategias transaccionales para asegurar que la eliminación se refleje correctamente en todos los componentes.

---

# Pruebas Unitarias

## VehicleServiceTest

Valida:

* Creación de vehículos.
* Consulta por ID.
* Eliminación de vehículos.
* Vehículos inexistentes.

## VehicleStatusServiceTest

Valida:

* EN_MOVIMIENTO.
* DETENIDO.
* SIN_SENAL.
* Cambios de estado.

## MetricsServiceTest

Valida:

* Conteo total de vehículos.
* Vehículos en movimiento.
* Vehículos detenidos.
* Vehículos sin señal.

## FleetTelemetrySystemApplicationTests

Valida:

* Carga correcta del contexto Spring Boot.

Ejecutar:

```bash
mvn test
```

Resultado:

```text
BUILD SUCCESS
```

---

# Docker

Construir imagen:

```bash
docker build -t fleet-telemetry-system .
```

Ejecutar contenedor:

```bash
docker run -d --name fleet-telemetry-system -p 8082:8081 fleet-telemetry-system
```

Verificar ejecución:

```bash
docker ps
```

API disponible en:

```text
http://localhost:8082
```

---

# Docker Compose

Levantar aplicación:

```bash
docker compose up -d
```

Detener aplicación:

```bash
docker compose down
```

Ver logs:

```bash
docker compose logs -f
```

API disponible en:

```text
http://localhost:8082
```

---

# Ejecución Rápida

```bash
git clone https://github.com/dabbi20/Sistema-de-Monitoreo-y-Telemetria-de-Flotas-GPS-Backend.git
cd Sistema-de-Monitoreo-y-Telemetria-de-Flotas-GPS-Backend
docker compose up -d
```

Backend disponible en:

```text
http://localhost:8082
```

---

# Funcionalidades Adicionales Implementadas

Además de los requisitos mínimos solicitados, se implementaron:

* Historial GPS por vehículo.
* Eventos de auditoría.
* Métricas operativas.
* Pruebas unitarias.
* Docker.
* Docker Compose.
* Diagramas UML.
* Manejo global de excepciones.
* Documentación técnica.

---

# Reporte de IA

## 1. ¿Qué herramientas de IA utilicé?

* ChatGPT
* GitHub Copilot

---

## 2. ¿Para qué tareas específicas me apoyé en IA?

* Generación inicial de clases y estructuras.
* Revisión de validaciones REST.
* Apoyo para pruebas unitarias.
* Documentación técnica.
* Revisión de buenas prácticas Spring Boot.

Todas las sugerencias fueron revisadas y adaptadas antes de ser incorporadas al proyecto.

---

## 3. ¿Qué error de la IA encontré y cómo lo corregí?

Durante el desarrollo algunas sugerencias iniciales no coincidían completamente con las reglas de negocio requeridas para la prueba.

Por ejemplo, algunas propuestas marcaban un vehículo como DETENIDO inmediatamente al recibir coordenadas repetidas. Después de revisar el enunciado y validar el comportamiento esperado, se ajustó la lógica para que el cambio ocurriera únicamente después de acumular al menos 60 segundos consecutivos sin movimiento.

También fue necesario corregir manualmente configuraciones de pruebas unitarias e integración entre frontend y backend.

Esto reforzó la importancia de utilizar la IA como una herramienta de apoyo y validar siempre sus resultados antes de incorporarlos al proyecto.

---

# Video de Sustentación

Enlace:

https://youtu.be/HmQwG2waTMU

---

# Autor

## David Carrasco

Ingeniero de Sistemas

Especialización en Desarrollo de Software y Automatizaciones

Desarrollador Full Stack

GitHub:

https://github.com/dabbi20
