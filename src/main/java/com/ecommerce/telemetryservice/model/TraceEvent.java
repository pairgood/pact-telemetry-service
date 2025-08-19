package com.ecommerce.telemetryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "trace_events")
public class TraceEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String traceId;
    
    @NotNull
    private String spanId;
    
    private String parentSpanId;
    
    @NotNull
    private String serviceName;
    
    @NotNull
    private String operation;
    
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    
    @NotNull
    private LocalDateTime timestamp;
    
    private Long durationMs;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    private String httpMethod;
    private String httpUrl;
    private Integer httpStatusCode;
    private String errorMessage;
    private String userId;
    private String correlationId;

    public TraceEvent() {
        this.timestamp = LocalDateTime.now();
        this.status = Status.SUCCESS;
        this.eventType = EventType.SPAN;
    }

    public TraceEvent(String traceId, String spanId, String serviceName, String operation) {
        this();
        this.traceId = traceId;
        this.spanId = spanId;
        this.serviceName = serviceName;
        this.operation = operation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public String getSpanId() { return spanId; }
    public void setSpanId(String spanId) { this.spanId = spanId; }
    
    public String getParentSpanId() { return parentSpanId; }
    public void setParentSpanId(String parentSpanId) { this.parentSpanId = parentSpanId; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    
    public String getHttpUrl() { return httpUrl; }
    public void setHttpUrl(String httpUrl) { this.httpUrl = httpUrl; }
    
    public Integer getHttpStatusCode() { return httpStatusCode; }
    public void setHttpStatusCode(Integer httpStatusCode) { this.httpStatusCode = httpStatusCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public enum EventType {
        SPAN, LOG, METRIC
    }

    public enum Status {
        SUCCESS, ERROR, TIMEOUT
    }
}