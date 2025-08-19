package com.ecommerce.telemetryservice.dto;

import com.ecommerce.telemetryservice.model.TraceEvent;

import java.time.LocalDateTime;

public class TraceEventDto {
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String serviceName;
    private String operation;
    private TraceEvent.EventType eventType;
    private LocalDateTime timestamp;
    private Long durationMs;
    private TraceEvent.Status status;
    private String metadata;
    private String httpMethod;
    private String httpUrl;
    private Integer httpStatusCode;
    private String errorMessage;
    private String userId;
    private String correlationId;

    public TraceEventDto() {}

    // Getters and Setters
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
    
    public TraceEvent.EventType getEventType() { return eventType; }
    public void setEventType(TraceEvent.EventType eventType) { this.eventType = eventType; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    
    public TraceEvent.Status getStatus() { return status; }
    public void setStatus(TraceEvent.Status status) { this.status = status; }
    
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
}