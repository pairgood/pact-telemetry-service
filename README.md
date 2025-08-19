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

## Related Services

- **[User Service](../user-service/README.md)**: Sends user authentication traces
- **[Product Service](../product-service/README.md)**: Sends product catalog traces
- **[Order Service](../order-service/README.md)**: Sends order processing and inter-service call traces
- **[Payment Service](../payment-service/README.md)**: Sends payment processing traces
- **[Notification Service](../notification-service/README.md)**: Sends notification delivery traces