# Smart Campus API Coursework Report (5COSC022W)

## 1) API Design Overview

This project implements a RESTful Smart Campus backend using only the coursework-approved stack:

- Java with JAX-RS (`javax.ws.rs`)
- Jersey runtime
- In-memory data structures (`ConcurrentHashMap`, `ArrayList`)
- Maven WAR packaging for Apache Tomcat

No Spring Boot and no database technology are used.

### Core Resources

- `Room`: `id`, `name`, `capacity`, `sensorIds`
- `Sensor`: `id`, `type`, `status`, `currentValue`, `roomId`
- `SensorReading`: `id`, `timestamp`, `value`

### Resource Hierarchy

- `/api/v1` (discovery endpoint)
- `/api/v1/rooms`
- `/api/v1/sensors`
- `/api/v1/sensors/{sensorId}/readings`

### Data Consistency Rules

- A sensor can only be created if its `roomId` exists.
- A room cannot be deleted if it still has assigned sensors.
- Posting a reading updates sensor reading history and the parent sensor `currentValue`.

## 2) Build and Run (Step-by-Step)

### Option A: NetBeans + Tomcat (recommended)

1. Open `smart_Campus_API` as a Maven project in NetBeans.
2. Register Apache Tomcat in NetBeans (Tomcat 9.x recommended).
3. Set project server to Tomcat.
4. Build and run the project from NetBeans.
5. Confirm app deploys under:
   - `http://localhost:8080/smart-campus-api/`
6. Test API root:
   - `GET http://localhost:8080/smart-campus-api/api/v1`

### Option B: Command line build

From `smart_Campus_API`:

```bash
mvn clean package
```

WAR output:

- `target/smart-campus-api-1.0.0.war`

Deploy this WAR to Tomcat and use:

- `http://localhost:8080/smart-campus-api/api/v1`

## 3) Implemented Endpoints

- `GET /api/v1`
- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `DELETE /api/v1/rooms/{roomId}`
- `GET /api/v1/sensors`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{sensorId}`
- `GET /api/v1/sensors/{sensorId}/readings`
- `POST /api/v1/sensors/{sensorId}/readings`

## 4) Error Handling and Logging

### Exception Mapping

- `409 Conflict` - room deletion blocked while sensors are still assigned
- `422 Unprocessable Entity` - sensor creation with non-existing linked `roomId`
- `403 Forbidden` - reading submission blocked for `MAINTENANCE` sensor
- `404 Not Found` - unknown room/sensor route or resource
- `500 Internal Server Error` - catch-all mapper for unexpected runtime failures

All mapped errors return JSON responses.

### Logging

A JAX-RS filter logs:

- incoming request method + URI
- outgoing response method + path + status

## 5) Sample cURL Commands (At Least Five)

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1
```

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":120,\"sensorIds\":[]}"
```

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":400.0,\"roomId\":\"LIB-301\"}"
```

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":500.5}"
```

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings
```

## 6) Postman Testing Steps (Simple Flow)

1. Open Postman and create collection `Smart Campus API Tests`.
2. Add collection variable:
   - `baseUrl = http://localhost:8080/smart-campus-api/api/v1`
3. Run this order:
   - `GET {{baseUrl}}`
   - `GET {{baseUrl}}/rooms`
   - `POST {{baseUrl}}/rooms`
   - `POST {{baseUrl}}/sensors`
   - `GET {{baseUrl}}/sensors?type=CO2`
   - `POST {{baseUrl}}/sensors/CO2-001/readings`
   - `GET {{baseUrl}}/sensors/CO2-001/readings`
   - error checks for `409`, `422`, `403`, `404`
4. For each request capture:
   - endpoint + method
   - request body (if any)
   - expected result
   - actual result
   - screenshot of status + response body


## 7) Answers to Coursework Questions

### Part 1.1 - Resource lifecycle and synchronization

JAX-RS resources are typically request-scoped by default. To preserve shared state safely across requests, this implementation stores data in centralized in-memory structures (`ConcurrentHashMap`) rather than resource instance fields.

### Part 1.2 - Why hypermedia is a hallmark of advanced REST

Hypermedia allows clients to discover available actions from response links, reducing hard-coded client routing and improving adaptability when APIs evolve.

### Part 2.1 - Returning IDs only vs full room objects

IDs only reduce payload size and bandwidth but may require extra client calls. Full objects increase payload size but reduce follow-up requests and can simplify client-side logic.

### Part 2.2 - Is DELETE idempotent

Yes. Repeating `DELETE /rooms/{roomId}` results in the same final state (room absent), which satisfies idempotency.

### Part 3.1 - Wrong content type with `@Consumes(APPLICATION_JSON)`

If a client sends another media type (for example `text/plain`), JAX-RS media matching fails for that method and typically returns `415 Unsupported Media Type`.

### Part 3.2 - Why query parameter is better for filtering

Filtering is best represented as a query on a collection (for example `/sensors?type=CO2`). Query parameters are cleaner and more composable than path-based filter patterns.

### Part 4.1 - Benefit of sub-resource locator pattern

Sub-resource locators keep nested functionality modular by delegating `/sensors/{id}/readings` to a dedicated class, improving maintainability and readability.

### Part 4.2 - Reading POST side effect on parent sensor

After storing a reading, the parent sensor `currentValue` is updated to the latest reading value to keep summary and history consistent.

### Part 5.2 - Why `422` is often more accurate than `404`

`422` is appropriate when request JSON is syntactically valid but semantically invalid (for example invalid linked `roomId`). `404` is usually for missing URL-targeted resources.

### Part 5.4 - Security risk of exposing stack traces

Raw stack traces can reveal internal class names, package paths, and framework details that attackers can use for targeted probing and exploitation.

### Part 5.5 - Why filters for logging

Filters centralize cross-cutting concerns, ensuring consistent logging across endpoints and avoiding repeated logger code in each resource method.

