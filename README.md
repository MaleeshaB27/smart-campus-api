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

## Part 1 - Service Architecture & Setup
Q1.1 - JAX-RS Resource Lifecycle: Per-Request vs Singleton

By default, JAX-RS mandates a per-request lifecycle: Jersey creates a new resource class instance for every HTTP request and discards it once the response is sent. Any instance field would therefore be lost between requests. To persist data across requests, state must live in a static, application-scoped structure. This project uses a DataStore utility class with three static final ConcurrentHashMap fields (ROOMS, SENSORS, SENSOR_READINGS). Being static, they are initialized once at class-load time and survive for the entire application lifetime. ConcurrentHashMap is chosen over plain HashMap to prevent race conditions: Tomcat services requests on multiple threads simultaneously, and ConcurrentHashMap's fine-grained locking ensures that concurrent get/put/remove operations are atomic and cannot corrupt internal state.

Q1.2 - HATEOAS and the Value of Hypermedia Links

HATEOAS (Hypermedia As The Engine Of Application State) requires servers to embed navigation links in responses so clients can discover all available actions without relying on static documentation. The Discovery endpoint at GET /api/v1 demonstrates this by returning a resources map with canonical URLs for every collection. 
Benefits over static documentation: 

• Discoverability: A client knowing only the root URL can navigate the entire API by following embedded links.

• Evolvability: URL changes are automatically reflected in responses; hard-coded client paths do not break. 

• Self-describing state: Responses can include only links valid for the current resource state, guiding clients toward legal transitions.

## Part 2 - Room Management
Q2.1 - Full Room Objects vs Returning Only IDs

Returning only IDs keeps the list payload small, but forces clients that need room details to issue one extra GET request per room  the classic N+1 problem  significantly increasing latency and server load for large datasets. Returning full objects (the chosen approach) delivers all necessary data in a single round-trip, ideal for dashboards displaying room names, capacities, and sensor counts. The trade-off is a larger payload, which can be addressed with pagination or sparse fieldsets if the dataset grows.

Q2.2 - Is DELETE Idempotent?

Yes. HTTP (RFC 9110) defines an idempotent method as one where multiple identical requests leave the server in the same state as a single request. In RoomResource.deleteRoom(), if the room is already absent, the method returns HTTP 204 No Content – the same status as a successful deletion  without throwing an error. The server state (room absent) is identical regardless of how many times the request is repeated, fully satisfying the idempotency contract.

## Part 3 - Sensor Operations & Linking
Q3.1 - Consequences of Sending a Non-JSON Content-Type

The @Consumes(MediaType.APPLICATION_JSON) annotation declares that the endpoint only accepts application/json request bodies. If a client sends text/plain or application/xml, JAX-RS performs content negotiation before invoking the method and immediately returns HTTP 415 Unsupported Media Type. The resource method body is never executed and no deserialization is attempted. This enforces a strict interface contract and prevents malformed payloads from reaching business logic.

Q3.2 - @QueryParam Filtering vs Path-Segment Filtering

Query parameters are the correct choice for filtering collections for several reasons: 

• Semantic correctness: A path segment implies a distinct, addressable resource. A filtered list is a view of the collection, not a new resource; query parameters represent this accurately. 

• Composability: Multiple filters combine naturally: ?type=CO2&status=ACTIVE. Path-based filtering makes this awkward and brittle. 

• Optionality: Omitting the parameter returns the full collection with no extra routing required. 

• REST convention: RFC 3986 distinguishes path (resource identity) from query (operation parameters). Filtering is parametric, so it belongs in the query string.

## Part 4 - Deep Nesting with Sub-Resources
Q4.1 - Architectural Benefits of the Sub-Resource Locator Pattern

A Sub-Resource Locator returns a Java object from a resource method; JAX-RS then uses that object as the root for further path matching. In this project, SensorResource.getSensorReadingResource(sensorId) instantiates and returns a SensorReadingResource, which handles GET and POST on /readings.
 The primary benefit over a monolithic controller is separation of concerns: each class has one responsibility, SensorResource manages sensors, SensorReadingResource manages readings making the code easier to read and test. Because the sensorId is passed directly to the sub-resource constructor, every method in SensorReadingResource already knows its parent context without re-parsing path parameters. This also means SensorReadingResource can be unit-tested by constructing it directly, without instantiating the parent resource at all. Finally, the pattern scales cleanly: adding new sub-resource types such as /alerts or /calibrations requires only a new class and a new locator method, keeping each file small and focused rather than inflating a single controller indefinitely.

## Part 5 - Error Handling, Exception Mapping & Logging
Q5.2 - Why HTTP 422 Is More Semantically Accurate Than 404

404 Not Found signals that the URL path does not map to an existing resource. The endpoint /api/v1/sensors is valid and exists, so returning 404 would mislead the client into thinking the endpoint itself is wrong. 422 Unprocessable Entity signals that the server understood the request the Content-Type is correct, the JSON is syntactically valid, the endpoint exists but could not process it due to a semantic validation failure within the payload. The roomId field references a non-existent entity, which is a business-logic error. 422 gives the client a precise, actionable signal: fix the referenced resource, not the URL.

Q5.4 - Cybersecurity Risks of Exposing Stack Traces

Exposing raw Java stack traces is an information disclosure vulnerability (OWASP A05). Exact framework and library versions visible in the trace (e.g., Jersey 2.41, Jackson 2.15.2) allow an attacker to look up published CVEs and craft targeted exploits. Package and class names reveal the internal structure of the application, helping identify high-value targets, while absolute file-system paths expose server topology and deployment configuration. Beyond infrastructure details, line numbers and method names allow an attacker to infer conditional business logic and discover exploitable edge cases. 
The GenericExceptionMapper mitigates all of this by intercepting every unhandled Throwable and returning a generic HTTP 500 with the message "An unexpected error occurred." The full trace is written to the server-side log only, where it is accessible to authorised operators but never exposed to external callers.

Q5.5 - Why Filters Are Superior to Manual Logger Calls

Manually inserting log statements into every resource method is an anti-pattern. Because logging is a cross-cutting concern that applies uniformly to every request and response, it belongs in a JAX-RS filter rather than scattered across individual methods. A single @Provider-annotated filter class applies automatically to every request, meaning a format change is made in one place rather than across every method in the codebase. It also guarantees complete coverage: a developer writing a new resource method cannot accidentally omit the log call, because the filter executes unconditionally. This keeps resource methods focused purely on business logic, with filters handling observability as a separate concern. 
In this project, ApiLoggingFilter implements both ContainerRequestFilter and ContainerResponseFilter, logging the HTTP method, URI, and response status for every request with zero changes to any resource class.


