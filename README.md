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

## 7) Test Evidence Table

| Test ID | Request (Endpoint + Method) | Request Body (if any) | Expected Result | Actual Result | Pass/Fail |
|---|---|---|---|---|---|
| P1-T1 | `GET /api/v1` | N/A | `200`, discovery JSON | _fill_ | _fill_ |
| P2-T1 | `GET /api/v1/rooms` | N/A | `200`, rooms list | _fill_ | _fill_ |
| P2-T2 | `POST /api/v1/rooms` | Room JSON | `201`, room created | _fill_ | _fill_ |
| P2-T3 | `GET /api/v1/rooms/LIB-301` | N/A | `200`, room returned | _fill_ | _fill_ |
| P3-T1 | `POST /api/v1/sensors` | Sensor JSON | `201`, sensor created | _fill_ | _fill_ |
| P3-T2 | `GET /api/v1/sensors?type=CO2` | N/A | `200`, filtered list | _fill_ | _fill_ |
| P4-T1 | `POST /api/v1/sensors/CO2-001/readings` | Reading JSON | `201`, reading created | _fill_ | _fill_ |
| P4-T2 | `GET /api/v1/sensors/CO2-001/readings` | N/A | `200`, history returned | _fill_ | _fill_ |
| P5-T1 | `DELETE /api/v1/rooms/LIB-301` | N/A | `409`, room not empty | _fill_ | _fill_ |
| P5-T2 | `POST /api/v1/sensors` with invalid `roomId` | Sensor JSON | `422`, linked resource error | _fill_ | _fill_ |
| P5-T3 | `POST /api/v1/sensors/{id}/readings` (maintenance) | Reading JSON | `403`, forbidden | _fill_ | _fill_ |
| P5-T4 | `GET /api/v1/rooms/NO-SUCH-ROOM` | N/A | `404`, not found | _fill_ | _fill_ |

## 8) Screenshot Placeholders

### Screenshot 1 - Discovery success (`GET /api/v1`, status + body)

_[paste screenshot here]_

### Screenshot 2 - Room creation success (`POST /rooms`, request + response)

_[paste screenshot here]_

### Screenshot 3 - Sensor creation success (`POST /sensors`, request + response)

_[paste screenshot here]_

### Screenshot 4 - Sensor filtering success (`GET /sensors?type=CO2`)

_[paste screenshot here]_

### Screenshot 5 - Reading creation success (`POST /sensors/{sensorId}/readings`)

_[paste screenshot here]_

### Screenshot 6 - Conflict error (`409` room not empty)

_[paste screenshot here]_

### Screenshot 7 - Linked resource error (`422` invalid `roomId`)

_[paste screenshot here]_

### Screenshot 8 - Forbidden error (`403` maintenance sensor)

_[paste screenshot here]_

### Screenshot 9 - Not found error (`404` missing resource)

_[paste screenshot here]_

### Screenshot 10 - Request/response log output in terminal

_[paste screenshot here]_

## 9) Answers to Coursework Questions

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

## 10) Submission Checklist

- Public GitHub repository link
- `README.md` includes:
  - API design overview
  - explicit build/run steps
  - at least five cURL commands
  - answers to coursework questions
  - evidence table and screenshot placeholders
- Postman video demo recorded (you visible and speaking)
- Blackboard submission completed with required links/files
# Smart Campus API Coursework Report (5COSC022W)

## 1) API Design Overview

This project implements a RESTful Smart Campus backend using only the coursework-approved stack:

- Java with JAX-RS (`javax.ws.rs`)
- Jersey runtime
- In-memory data structures (`ConcurrentHashMap`, `ArrayList`)
- Maven WAR packaging for Apache Tomcat

No Spring Boot and no database technology are used.

### Core Resources

- `Room`
  - `id`, `name`, `capacity`, `sensorIds`
- `Sensor`
  - `id`, `type`, `status`, `currentValue`, `roomId`
- `SensorReading`
  - `id`, `timestamp`, `value`

### Resource Hierarchy

- `/api/v1` (discovery)
- `/api/v1/rooms`
- `/api/v1/sensors`
- `/api/v1/sensors/{sensorId}/readings`

### Data Consistency Rules

- A sensor can only be created if its `roomId` exists.
- A room cannot be deleted if it still has assigned sensors.
- Posting a reading updates both reading history and parent sensor `currentValue`.

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

- `409 Conflict` - Room deletion blocked when sensors still assigned
- `422 Unprocessable Entity` - Sensor creation with non-existing `roomId`
- `403 Forbidden` - Reading POST blocked when sensor status is `MAINTENANCE`
- `404 Not Found` - Unknown room/sensor resource
- `500 Internal Server Error` - Catch-all global mapper for unexpected runtime errors

All mapped errors return JSON responses.

### Logging

A JAX-RS filter logs:

- incoming request method + URI
- outgoing response method + path + status

## 5) Sample cURL Commands (Requirement: At Least Five)

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

## 6) Postman Testing Steps (Simple Flow for Video)

1. Open Postman and create a collection `Smart Campus API Tests`.
2. Add collection variable:
   - `baseUrl = http://localhost:8080/smart-campus-api/api/v1`
3. Run in this order:
   - `GET {{baseUrl}}`
   - `GET {{baseUrl}}/rooms`
   - `POST {{baseUrl}}/rooms`
   - `POST {{baseUrl}}/sensors`
   - `GET {{baseUrl}}/sensors?type=CO2`
   - `POST {{baseUrl}}/sensors/CO2-001/readings`
   - `GET {{baseUrl}}/sensors/CO2-001/readings`
   - error tests: `409`, `422`, `403`, `404`
4. During each request, show:
   - URL + method
   - request body (if any)
   - response status
   - response body



## 9) Answers to Coursework Questions

### Part 1.1 - Resource lifecycle and synchronization

In standard JAX-RS behavior, resources are typically request-scoped unless configured otherwise. To preserve data between requests and reduce race-condition risk, this implementation stores shared state in centralized in-memory structures (`ConcurrentHashMap`) instead of resource instance fields.

### Part 1.2 - Why hypermedia is a hallmark of advanced REST

Hypermedia helps clients discover available actions dynamically from responses, rather than relying only on static documentation. This improves client adaptability when APIs evolve.

### Part 2.1 - IDs only vs full room objects in list responses

IDs-only responses reduce payload size and network usage but require extra client calls for details. Full objects increase payload size but reduce follow-up calls and simplify clients.

### Part 2.2 - Is DELETE idempotent

Yes. Sending the same `DELETE /rooms/{roomId}` multiple times leaves the final state unchanged (room absent), which is idempotent behavior.

### Part 3.1 - Consequence of wrong content type with `@Consumes(APPLICATION_JSON)`

If a client sends another media type (for example `text/plain`), JAX-RS media type matching fails for that method, and the framework typically responds with `415 Unsupported Media Type`.

### Part 3.2 - Why query param is better for filtering than path segment

Filtering is naturally expressed as a query on a collection (`/sensors?type=CO2`). Query parameters are composable and clearer for search/filter concerns than path-based pseudo-resources.

### Part 4.1 - Benefit of sub-resource locator pattern

Sub-resource locators separate nested endpoint logic into dedicated classes. This keeps code modular, easier to maintain, and avoids very large controller classes.

### Part 4.2 - Reading POST side effect on parent sensor

After successful reading insertion, the parent sensor `currentValue` is updated to the latest reading value to keep summary and historical data consistent.

### Part 5.2 - Why `422` is often more accurate than `404`

`422` indicates the request payload is syntactically valid but semantically invalid (for example, referencing a non-existing linked resource). `404` is mainly for missing endpoint/resource addressed by the URL itself.

### Part 5.4 - Security risks of exposing stack traces

Raw stack traces can reveal class names, package structure, framework versions, and internal logic paths, which can help attackers profile and target the system.

### Part 5.5 - Why use filters for logging

Filters apply cross-cutting logging in one central place for every endpoint, reducing duplicated logger code in resource methods and improving consistency.

## 10) Submission Checklist

- Public GitHub repository link
- This `README.md` contains:
  - API design overview
  - build and run steps
  - at least five curl examples
  - answers to questions in each coursework part
  - testing evidence table and screenshot placeholders
- Postman video demonstration recorded (you visible and speaking clearly)
- Blackboard submission completed with required links/files
# Smart Campus API Coursework (5COSC022W)

This project implements a RESTful **Smart Campus Sensor and Room Management API** using the coursework-required stack:

- Java with JAX-RS (`javax.ws.rs`)
- Jersey runtime
- In-memory collections only (`ConcurrentHashMap`, `ArrayList`)
- Maven WAR packaging for Apache Tomcat deployment

No Spring Boot and no database technologies are used.

## API Design Overview

The API models a physical campus hierarchy:

- A `Room` contains zero or more sensors (`sensorIds`).
- A `Sensor` belongs to one room (`roomId`) and keeps a live `currentValue`.
- A `SensorReading` is historical data for a sensor, accessed as a nested resource.

Resource hierarchy:

- `/api/v1/rooms`
- `/api/v1/sensors`
- `/api/v1/sensors/{sensorId}/readings`

Base URL when deployed locally:

- `http://localhost:8080/smart-campus-api/api/v1`

## Endpoints Implemented

- `GET /api/v1` - Discovery metadata endpoint
- `GET /api/v1/rooms` - List rooms
- `POST /api/v1/rooms` - Create room
- `GET /api/v1/rooms/{roomId}` - Get room detail
- `DELETE /api/v1/rooms/{roomId}` - Delete room (blocked if room still has sensors)
- `GET /api/v1/sensors` - List sensors (supports optional `?type=...`)
- `POST /api/v1/sensors` - Create sensor (validates `roomId`)
- `GET /api/v1/sensors/{sensorId}` - Get sensor detail
- `GET /api/v1/sensors/{sensorId}/readings` - Get reading history
- `POST /api/v1/sensors/{sensorId}/readings` - Add reading and update parent sensor `currentValue`

## Error Handling and Logging

Exception mappers:

- `409 Conflict` - `RoomNotEmptyException`
- `422 Unprocessable Entity` - `LinkedResourceNotFoundException`
- `403 Forbidden` - `SensorUnavailableException`
- `404 Not Found` - `NotFoundException`
- `500 Internal Server Error` - global `ExceptionMapper<Throwable>`

All mapped errors return JSON entities.

Observability:

- A JAX-RS request/response filter logs:
  - incoming HTTP method + URI
  - outgoing method + path + final status code



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

## Conceptual Report Answers (Coursework Questions)

### Part 1.1 - JAX-RS resource lifecycle and in-memory synchronization

By default, JAX-RS creates a new resource instance per request unless configured otherwise by the runtime or dependency injection container. In this project, shared state is held in static in-memory structures (`ConcurrentHashMap`) in a dedicated repository class rather than in resource instance fields. This avoids losing state across requests and reduces race-condition risk under concurrent access.

### Part 1.2 - Why hypermedia improves REST design

Hypermedia (HATEOAS) makes responses self-describing by including navigation links. Clients can discover available actions dynamically instead of hardcoding endpoint paths from static docs only. This reduces client breakage when APIs evolve and improves developer usability.

### Part 2.1 - Returning room IDs only vs full room objects

Returning only IDs minimizes payload size and network bandwidth, which is efficient for large collections. Returning full room objects reduces follow-up requests and simplifies client logic. The better choice depends on use case: list views often prefer compact summaries, while detail views need full objects.

### Part 2.2 - Is DELETE idempotent here

Yes. Repeating `DELETE /rooms/{roomId}` yields the same final server state: the room is absent. In this implementation, deleting a non-existing room returns `204 No Content`, preserving idempotent behavior.

### Part 3.1 - Consequences of wrong content type with @Consumes(JSON)

If a client sends `text/plain` or XML to a method that consumes JSON, JAX-RS media type matching fails before business logic executes. The runtime typically returns `415 Unsupported Media Type` (or a mapper-defined equivalent), signaling that the payload format is unsupported for that endpoint.

### Part 3.2 - Why query params are better for filtering

Filtering is a modifier on a collection resource, so query parameters (`/sensors?type=CO2`) are semantically appropriate and composable (e.g., multiple filters). Encoding filters in path segments (`/sensors/type/CO2`) treats each filter variant like a separate resource path, which scales poorly and is less flexible.

### Part 4.1 - Benefits of sub-resource locator pattern

Sub-resource locators keep nested concerns modular by delegating `/sensors/{id}/readings` logic to a dedicated class. This improves readability, separation of concerns, and maintainability. It avoids large controller classes containing unrelated nested routing logic.

### Part 4.2 - Reading POST side effect consistency

When a new reading is posted, the reading is appended to sensor history and the parent sensor `currentValue` is updated to the latest reading value. This keeps summary sensor state and historical data synchronized across API responses.

### Part 5.2 - Why 422 is more accurate than 404 for missing linked reference

`422 Unprocessable Entity` is appropriate when the request syntax is valid JSON but business validation fails (e.g., referenced `roomId` does not exist). `404` usually describes the requested URL resource not existing, not a semantic issue inside a valid payload.

### Part 5.4 - Security risks of exposing raw stack traces

Raw stack traces can leak internal package names, class names, method paths, framework versions, and code structure. Attackers can use this intelligence for targeted exploitation, endpoint probing, and vulnerability matching. A generic `500` response reduces information exposure.

### Part 5.5 - Why filters are better for cross-cutting logging

Filters centralize logging in one place and apply consistently to every endpoint. This avoids duplicated logging code inside each resource method, reduces maintenance overhead, and ensures uniform observability behavior.

## Submission Checklist

- Public GitHub repository link available
- README contains architecture, build/run steps, curl samples, and report answers
- Postman demonstration video recorded (max 10 minutes), with you visible and speaking
- Blackboard submission includes required links/files
