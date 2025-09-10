# Telemetry Service

> **📊 This service is highlighted in the architecture diagram below**

Distributed tracing and observability service for the e-commerce microservices ecosystem.

## Service Role: Consumer Only
This service collects telemetry data, traces, and metrics from all other services but does not produce data for other services to consume.

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐
│   User Service  │    │ Product Service │
│   (Port 8081)   │    │   (Port 8082)   │
│                 │    │                 │
│ • Authentication│    │ • Product Catalog│
│ • User Profiles │    │ • Inventory Mgmt│
│ • JWT Tokens    │    │ • Pricing       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          │ validates users      │ fetches products
          │                      │
          ▼                      ▼
    ┌─────────────────────────────────┐
    │        Order Service            │
    │        (Port 8083)              │
    │                                 │
    │ • Order Management              │
    │ • Order Processing              │
    │ • Consumes User & Product APIs  │
    └─────────────┬───────────────────┘
                  │
                  │ triggers payment
                  │
                  ▼
    ┌─────────────────────────────────┐
    │       Payment Service           │
    │       (Port 8084)               │
    │                                 │
    │ • Payment Processing            │
    │ • Gateway Integration           │
    │ • Refund Management             │
    └─────────────┬───────────────────┘
                  │
                  │ sends notifications
                  │
                  ▼
    ┌─────────────────────────────────┐
    │    Notification Service         │
    │       (Port 8085)               │
    │                                 │
    │ • Email Notifications           │
    │ • SMS Notifications             │
    │ • Order & Payment Updates       │
    └─────────────────────────────────┘
                  │
                  │ All services send telemetry data
                  │
                  ▼
    ┌─────────────────────────────────┐
    │📊  Telemetry Service            │
    │       (Port 8086)               │
    │                                 │
    │ • Distributed Tracing           │
    │ • Service Metrics               │
    │ • Request Tracking              │
    │ • Performance Monitoring        │
    └─────────────────────────────────┘
```

## Features

- **Distributed Tracing**: End-to-end request tracing across all services
- **Service Metrics**: Performance metrics and statistics for each service
- **Timeline Visualization**: Trace timeline showing service call sequences
- **Error Tracking**: Capture and analyze errors across the system  
- **Service Discovery**: Automatic discovery and monitoring of active services
- **Health Monitoring**: System-wide health and status monitoring
- **Data Retention**: Configurable cleanup of old trace data

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Monitoring**: Spring Boot Actuator
- **Java Version**: 17

## API Endpoints

### Trace Collection
- `POST /api/telemetry/events` - Record single telemetry event
- `POST /api/telemetry/events/batch` - Record multiple events in batch

### Trace Retrieval
- `GET /api/telemetry/traces/{traceId}` - Get complete trace by ID
- `GET /api/telemetry/traces/{traceId}/timeline` - Get trace timeline with duration analysis
- `GET /api/telemetry/traces` - Search and filter traces with pagination

### Service Analytics
- `GET /api/telemetry/services` - List all tracked services
- `GET /api/telemetry/services/{serviceName}/operations` - Get operations for a service
- `GET /api/telemetry/services/{serviceName}/metrics` - Get performance metrics for a service

### System Management
- `GET /api/telemetry/health` - Get telemetry system health status
- `DELETE /api/telemetry/traces/cleanup` - Cleanup old traces

## Running the Service

### Prerequisites
- Java 17+
- Gradle (or use included Gradle wrapper)

### Start the Service
```bash
./gradlew bootRun
```

The service will start on **port 8086**.

### Database Access
- **H2 Console**: http://localhost:8086/h2-console
- **JDBC URL**: `jdbc:h2:mem:telemetrydb`
- **Username**: `sa`
- **Password**: (empty)

## Service Dependencies

### Services That Use This Service
- **User Service**: Sends authentication and user management traces
- **Product Service**: Sends product catalog and inventory traces  
- **Order Service**: Sends order processing traces and inter-service call traces
- **Payment Service**: Sends payment processing and gateway traces
- **Notification Service**: Sends notification delivery traces

### External Dependencies
- None (this is a consumer-only service)

## Trace Data Model

### TraceEvent Entity
```json
{
  "id": 1,
  "traceId": "trace_abc123def456",
  "spanId": "span_789xyz",
  "parentSpanId": "span_parent123",
  "serviceName": "order-service",
  "operation": "create_order",
  "eventType": "SPAN",
  "timestamp": "2024-01-15T10:30:00",
  "durationMs": 1250,
  "status": "SUCCESS",
  "httpMethod": "POST",
  "httpUrl": "http://localhost:8083/api/orders",
  "httpStatusCode": 200,
  "userId": "123",
  "metadata": "Order created successfully"
}
```

### Event Types
- `SPAN` - Request/operation span with duration
- `LOG` - Log event within a trace
- `METRIC` - Metric data point

### Status Values
- `SUCCESS` - Operation completed successfully
- `ERROR` - Operation failed
- `TIMEOUT` - Operation timed out

## Example Usage

### Get All Services
```bash
curl -X GET http://localhost:8086/api/telemetry/services
```

### Get Trace Timeline
```bash
curl -X GET http://localhost:8086/api/telemetry/traces/trace_abc123def456/timeline
```

### Get Service Metrics
```bash
curl -X GET http://localhost:8086/api/telemetry/services/order-service/metrics
```

### Search Traces
```bash
curl -X GET "http://localhost:8086/api/telemetry/traces?serviceName=order-service&status=ERROR&page=0&size=10"
```

## Telemetry Integration

All services in the ecosystem automatically send telemetry data to this service using the `TelemetryClient` class. The integration includes:

### Automatic Trace Generation
- **Trace ID**: Unique identifier for each request flow
- **Span ID**: Unique identifier for each service operation
- **Parent-Child Relationships**: Links between service calls

### Inter-Service Call Tracking
- Outbound HTTP requests between services
- Response times and status codes
- Error tracking and propagation

### Performance Metrics
- Request duration measurements
- Success/error rates per operation
- Service-level aggregated statistics

## Console Output

When telemetry events are received, you'll see console output like:

```
📊 Telemetry Event Recorded:
  Service: order-service
  Operation: create_order
  Trace ID: trace_abc123def456
  Span ID: span_789xyz
  Parent Span: span_parent123
  Duration: 1250ms
  Status: SUCCESS
```

## Production Considerations

In a production environment, this service would typically:
- Use persistent storage (PostgreSQL, Elasticsearch)
- Implement data partitioning and archiving
- Add authentication and authorization
- Scale horizontally for high throughput
- Integrate with monitoring dashboards (Grafana, Kibana)
- Export data to external observability platforms

## Pact Contract Testing

This service uses [Pact](https://pact.io/) for consumer contract testing to ensure reliable communication with external services.

### Consumer Role

This service acts as a consumer for external services when they are integrated. Currently, the telemetry service has **no external HTTP dependencies** but the Pact infrastructure is established for future integrations.

### Running Pact Tests

#### Consumer Tests
```bash
# Run consumer tests and generate contracts
./gradlew pactTest

# Generated contracts will be in build/pacts/
```

#### Publishing Contracts
```bash
# Publish contracts to Pactflow
./gradlew pactPublish
```

### Contract Testing Approach

This implementation follows Pact's **"Be conservative in what you send"** principle:

- Consumer tests define minimal request structures with only required fields
- Request bodies cannot contain fields not defined in the contract
- Tests validate that actual API calls match contract expectations exactly
- Mock servers reject requests with unexpected extra fields

### Contract Files

Consumer contracts are generated in:
- `build/pacts/` - Local contract files  
- Pactflow - Centralized contract storage and management

### Future External Service Integration

When external services are added to the telemetry service (e.g., authentication, user service), implement consumer tests following this pattern:

#### Directory Structure
```
src/test/java/com/ecommerce/telemetryservice/pact/
├── config/
│   └── PactTestConfig.java          # Test configuration
├── UserServicePactTest.java         # Consumer tests for User Service
├── AuthServicePactTest.java         # Consumer tests for Auth Service
└── ExamplePactTestStructure.java    # Template/example (remove when used)
```

#### Consumer Test Template
```java
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.au.com.dius.pact=DEBUG"
})
class ExternalServicePactTest {

    @Pact(consumer = "telemetry-service", provider = "external-service-name")
    public RequestResponsePact methodNamePact(PactDslWithProvider builder) {
        return builder
            .given("provider state description")
            .uponReceiving("interaction description")
            .path("/api/endpoint")
            .method("GET")
            .headers(Map.of(
                "Content-Type", "application/json",
                "Accept", "application/json"
            ))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(LambdaDsl.newJsonBody((body) -> body
                .numberType("id")
                .stringType("status")
            ).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "methodNamePact")
    void testMethodName(MockServer mockServer) {
        // Arrange: Set up client with mock server URL
        ExternalServiceClient client = new ExternalServiceClient(mockServer.getUrl());
        
        // Act: Make the API call
        ResponseDto response = client.methodName();
        
        // Assert: Verify only the fields we actually use
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isNotNull();
    }
}
```

### Troubleshooting

#### Common Issues

1. **Consumer Test Failures**
   - **Extra fields in request**: Remove any fields from request body that aren't actually needed
   - **Mock server expectation mismatch**: Verify HTTP method, path, headers, and body structure
   - **Content-Type headers**: Ensure request headers match exactly what the service sends
   - **URL path parameters**: Check that path parameters are correctly formatted in the contract

2. **Contract Generation Issues**
   - **Missing @Pact annotation**: Ensure each contract method has proper annotations
   - **Invalid JSON structure**: Verify LambdaDsl body definitions match actual data structures
   - **Provider state setup**: Ensure provider state descriptions are descriptive and specific

3. **Pactflow Integration Issues**
   - **Authentication**: Verify `PACT_BROKER_TOKEN` environment variable is set
   - **Base URL**: Confirm `PACT_BROKER_BASE_URL` points to `https://pairgood.pactflow.io`
   - **Network connectivity**: Check firewall/proxy settings if publishing fails

#### Debug Commands

```bash
# Run with debug output
./gradlew pactTest --info --debug

# Run specific test class
./gradlew pactTest --tests="*UserServicePactTest*"

# Generate contracts without publishing
./gradlew pactTest -x pactPublish

# Clean and regenerate contracts
./gradlew clean pactTest
```

#### Debug Logging

Pact debug logging is configured in `src/test/resources/application-test.properties`:
```properties
logging.level.au.com.dius.pact=DEBUG
logging.level.org.apache.http=DEBUG
```

### Contract Evolution

When external services change their APIs:

1. **New Fields in Responses**: No action needed - consumers ignore extra fields
2. **Removed Response Fields**: Update consumer tests if those fields were being used
3. **New Required Request Fields**: Update consumer tests and service code
4. **Changed Endpoints**: Update consumer contract paths and service client code

### Integration with CI/CD

Consumer contract tests run automatically on:
- **Pull Requests**: Generate and validate contracts
- **Main Branch**: Publish contracts to Pactflow for provider verification
- **Feature Branches**: Generate contracts for validation (not published)

### Manual Testing

For local development against real services:
```bash
# Test against local services (disable Pact)
./gradlew test -Dpact.verifier.disabled=true

# Test against staging services
export EXTERNAL_SERVICE_URL=https://staging.example.com
./gradlew test -Dpact.verifier.disabled=true
```

### Contract Documentation

Generated contracts document:
- **API interactions**: What endpoints this service calls
- **Request formats**: Exact structure of requests sent
- **Response expectations**: What fields this service relies on
- **Error handling**: How this service handles different response scenarios

## Related Services

- **[User Service](../user-service/README.md)**: Sends user authentication traces
- **[Product Service](../product-service/README.md)**: Sends product catalog traces
- **[Order Service](../order-service/README.md)**: Sends order processing and inter-service call traces
- **[Payment Service](../payment-service/README.md)**: Sends payment processing traces
- **[Notification Service](../notification-service/README.md)**: Sends notification delivery traces