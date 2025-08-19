package com.ecommerce.telemetryservice.service;

import com.ecommerce.telemetryservice.dto.TraceEventDto;
import com.ecommerce.telemetryservice.model.TraceEvent;
import com.ecommerce.telemetryservice.repository.TraceEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TelemetryService {
    
    @Autowired
    private TraceEventRepository traceEventRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public TraceEvent recordEvent(TraceEventDto eventDto) {
        TraceEvent event = convertToEntity(eventDto);
        TraceEvent savedEvent = traceEventRepository.save(event);
        
        // Log the trace event for debugging
        System.out.println("📊 Telemetry Event Recorded:");
        System.out.println("  Service: " + event.getServiceName());
        System.out.println("  Operation: " + event.getOperation());
        System.out.println("  Trace ID: " + event.getTraceId());
        System.out.println("  Span ID: " + event.getSpanId());
        if (event.getParentSpanId() != null) {
            System.out.println("  Parent Span: " + event.getParentSpanId());
        }
        System.out.println("  Duration: " + (event.getDurationMs() != null ? event.getDurationMs() + "ms" : "N/A"));
        System.out.println("  Status: " + event.getStatus());
        
        return savedEvent;
    }
    
    public List<TraceEvent> recordEventsBatch(List<TraceEventDto> eventDtos) {
        List<TraceEvent> events = eventDtos.stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        
        List<TraceEvent> savedEvents = traceEventRepository.saveAll(events);
        
        System.out.println("📊 Batch Telemetry Events Recorded: " + savedEvents.size() + " events");
        
        return savedEvents;
    }
    
    public List<TraceEvent> getTraceById(String traceId) {
        return traceEventRepository.findByTraceIdOrderByTimestamp(traceId);
    }
    
    public Map<String, Object> getTraceTimeline(String traceId) {
        List<TraceEvent> events = getTraceById(traceId);
        
        if (events.isEmpty()) {
            return Map.of("traceId", traceId, "events", List.of(), "totalDuration", 0);
        }
        
        LocalDateTime startTime = events.get(0).getTimestamp();
        LocalDateTime endTime = events.get(events.size() - 1).getTimestamp();
        
        List<Map<String, Object>> timeline = events.stream()
            .map(event -> {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("spanId", event.getSpanId());
                eventMap.put("parentSpanId", event.getParentSpanId());
                eventMap.put("serviceName", event.getServiceName());
                eventMap.put("operation", event.getOperation());
                eventMap.put("timestamp", event.getTimestamp());
                eventMap.put("durationMs", event.getDurationMs());
                eventMap.put("status", event.getStatus());
                eventMap.put("httpMethod", event.getHttpMethod());
                eventMap.put("httpUrl", event.getHttpUrl());
                eventMap.put("httpStatusCode", event.getHttpStatusCode());
                eventMap.put("errorMessage", event.getErrorMessage());
                return eventMap;
            })
            .collect(Collectors.toList());
        
        long totalDurationMs = java.time.Duration.between(startTime, endTime).toMillis();
        
        return Map.of(
            "traceId", traceId,
            "events", timeline,
            "totalDuration", totalDurationMs,
            "startTime", startTime,
            "endTime", endTime,
            "serviceCount", events.stream().map(TraceEvent::getServiceName).distinct().count()
        );
    }
    
    public Page<Map<String, Object>> getTraces(PageRequest pageRequest, String serviceName, String operation, String status) {
        List<String> distinctTraceIds = traceEventRepository.findDistinctTraceIds(pageRequest);
        
        List<Map<String, Object>> traces = distinctTraceIds.stream()
            .map(traceId -> {
                List<TraceEvent> events = traceEventRepository.findByTraceIdOrderByTimestamp(traceId);
                
                if (serviceName != null && events.stream().noneMatch(e -> serviceName.equals(e.getServiceName()))) {
                    return null;
                }
                if (operation != null && events.stream().noneMatch(e -> operation.equals(e.getOperation()))) {
                    return null;
                }
                if (status != null && events.stream().noneMatch(e -> status.equals(e.getStatus().toString()))) {
                    return null;
                }
                
                TraceEvent firstEvent = events.get(0);
                TraceEvent lastEvent = events.get(events.size() - 1);
                
                long totalDuration = java.time.Duration.between(firstEvent.getTimestamp(), lastEvent.getTimestamp()).toMillis();
                
                Map<String, Object> traceSummary = new HashMap<>();
                traceSummary.put("traceId", traceId);
                traceSummary.put("startTime", firstEvent.getTimestamp());
                traceSummary.put("duration", totalDuration);
                traceSummary.put("services", events.stream().map(TraceEvent::getServiceName).distinct().collect(Collectors.toList()));
                traceSummary.put("operations", events.stream().map(TraceEvent::getOperation).distinct().collect(Collectors.toList()));
                traceSummary.put("spanCount", events.size());
                traceSummary.put("status", events.stream().anyMatch(e -> e.getStatus() == TraceEvent.Status.ERROR) ? "ERROR" : "SUCCESS");
                
                return traceSummary;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new PageImpl<>(traces, pageRequest, traces.size());
    }
    
    public List<String> getServices() {
        return traceEventRepository.findDistinctServiceNames();
    }
    
    public List<String> getServiceOperations(String serviceName) {
        return traceEventRepository.findDistinctOperationsByServiceName(serviceName);
    }
    
    public Map<String, Object> getServiceMetrics(String serviceName) {
        List<TraceEvent> events = traceEventRepository.findByServiceNameOrderByTimestampDesc(serviceName);
        
        if (events.isEmpty()) {
            return Map.of("serviceName", serviceName, "requestCount", 0);
        }
        
        long requestCount = events.size();
        double avgDuration = events.stream()
            .filter(e -> e.getDurationMs() != null)
            .mapToLong(TraceEvent::getDurationMs)
            .average()
            .orElse(0.0);
        
        long errorCount = events.stream()
            .filter(e -> e.getStatus() == TraceEvent.Status.ERROR)
            .count();
        
        double errorRate = requestCount > 0 ? (double) errorCount / requestCount * 100 : 0.0;
        
        Map<String, Long> operationCounts = events.stream()
            .collect(Collectors.groupingBy(TraceEvent::getOperation, Collectors.counting()));
        
        return Map.of(
            "serviceName", serviceName,
            "requestCount", requestCount,
            "averageDurationMs", Math.round(avgDuration),
            "errorCount", errorCount,
            "errorRate", Math.round(errorRate * 100.0) / 100.0,
            "operationCounts", operationCounts
        );
    }
    
    public Map<String, Object> getHealthStatus() {
        long totalEvents = traceEventRepository.count();
        long recentEvents = traceEventRepository.countRecentEvents(LocalDateTime.now().minusHours(1));
        List<String> services = getServices();
        
        return Map.of(
            "status", "healthy",
            "totalEvents", totalEvents,
            "recentEvents", recentEvents,
            "trackedServices", services.size(),
            "services", services,
            "timestamp", LocalDateTime.now()
        );
    }
    
    public Map<String, Object> cleanupOldTraces(int olderThanDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(olderThanDays);
        long deletedCount = traceEventRepository.deleteByTimestampBefore(cutoffDate);
        
        return Map.of(
            "deletedEvents", deletedCount,
            "cutoffDate", cutoffDate,
            "message", "Cleaned up traces older than " + olderThanDays + " days"
        );
    }
    
    private TraceEvent convertToEntity(TraceEventDto dto) {
        TraceEvent event = new TraceEvent();
        event.setTraceId(dto.getTraceId());
        event.setSpanId(dto.getSpanId());
        event.setParentSpanId(dto.getParentSpanId());
        event.setServiceName(dto.getServiceName());
        event.setOperation(dto.getOperation());
        event.setEventType(dto.getEventType() != null ? dto.getEventType() : TraceEvent.EventType.SPAN);
        event.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        event.setDurationMs(dto.getDurationMs());
        event.setStatus(dto.getStatus() != null ? dto.getStatus() : TraceEvent.Status.SUCCESS);
        event.setMetadata(dto.getMetadata());
        event.setHttpMethod(dto.getHttpMethod());
        event.setHttpUrl(dto.getHttpUrl());
        event.setHttpStatusCode(dto.getHttpStatusCode());
        event.setErrorMessage(dto.getErrorMessage());
        event.setUserId(dto.getUserId());
        event.setCorrelationId(dto.getCorrelationId());
        
        return event;
    }
}