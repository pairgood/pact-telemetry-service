package com.ecommerce.telemetryservice.controller;

import com.ecommerce.telemetryservice.dto.TraceEventDto;
import com.ecommerce.telemetryservice.model.TraceEvent;
import com.ecommerce.telemetryservice.service.TelemetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = "*")
@Tag(name = "Telemetry & Monitoring", description = "API for collecting, storing, and retrieving telemetry data, traces, and system metrics")
public class TelemetryController {
    
    @Autowired
    private TelemetryService telemetryService;
    
    @PostMapping("/events")
    @Operation(summary = "Record telemetry event", description = "Records a single telemetry event for tracing and monitoring purposes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid event data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TraceEvent> recordEvent(@RequestBody TraceEventDto eventDto) {
        TraceEvent event = telemetryService.recordEvent(eventDto);
        return ResponseEntity.ok(event);
    }
    
    @PostMapping("/events/batch")
    @Operation(summary = "Record batch telemetry events", description = "Records multiple telemetry events in a single batch operation for improved performance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid event data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TraceEvent>> recordEventsBatch(@RequestBody List<TraceEventDto> eventDtos) {
        List<TraceEvent> events = telemetryService.recordEventsBatch(eventDtos);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/traces/{traceId}")
    @Operation(summary = "Get trace by ID", description = "Retrieves all events associated with a specific trace identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trace events retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No events found for the specified trace ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TraceEvent>> getTraceById(
        @Parameter(description = "Unique identifier of the trace", required = true, example = "trace-123-abc")
        @PathVariable String traceId) {
        List<TraceEvent> trace = telemetryService.getTraceById(traceId);
        return ResponseEntity.ok(trace);
    }
    
    @GetMapping("/traces/{traceId}/timeline")
    @Operation(summary = "Get trace timeline", description = "Retrieves a chronological timeline view of all events in a specific trace")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trace timeline retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No timeline found for the specified trace ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getTraceTimeline(
        @Parameter(description = "Unique identifier of the trace", required = true, example = "trace-123-abc")
        @PathVariable String traceId) {
        Map<String, Object> timeline = telemetryService.getTraceTimeline(traceId);
        return ResponseEntity.ok(timeline);
    }
    
    @GetMapping("/traces")
    @Operation(summary = "Get paginated traces", description = "Retrieves a paginated list of traces with optional filtering by service, operation, and status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Traces retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<Map<String, Object>>> getTraces(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by service name", example = "user-service")
            @RequestParam(required = false) String serviceName,
            @Parameter(description = "Filter by operation name", example = "register_user")
            @RequestParam(required = false) String operation,
            @Parameter(description = "Filter by status code", example = "200")
            @RequestParam(required = false) String status) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Map<String, Object>> traces = telemetryService.getTraces(pageRequest, serviceName, operation, status);
        return ResponseEntity.ok(traces);
    }
    
    @GetMapping("/services")
    @Operation(summary = "Get all services", description = "Retrieves a list of all services that have recorded telemetry data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getServices() {
        List<String> services = telemetryService.getServices();
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/services/{serviceName}/operations")
    @Operation(summary = "Get service operations", description = "Retrieves all operation names for a specific service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service operations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Service not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getServiceOperations(
        @Parameter(description = "Name of the service", required = true, example = "user-service")
        @PathVariable String serviceName) {
        List<String> operations = telemetryService.getServiceOperations(serviceName);
        return ResponseEntity.ok(operations);
    }
    
    @GetMapping("/services/{serviceName}/metrics")
    @Operation(summary = "Get service metrics", description = "Retrieves performance and health metrics for a specific service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service metrics retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Service not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getServiceMetrics(
        @Parameter(description = "Name of the service", required = true, example = "user-service")
        @PathVariable String serviceName) {
        Map<String, Object> metrics = telemetryService.getServiceMetrics(serviceName);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Get system health status", description = "Retrieves overall health status and system information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Health status retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> health = telemetryService.getHealthStatus();
        return ResponseEntity.ok(health);
    }
    
    @DeleteMapping("/traces/cleanup")
    @Operation(summary = "Cleanup old traces", description = "Removes trace data older than the specified number of days to manage storage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cleanup completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid cleanup parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> cleanupOldTraces(
        @Parameter(description = "Remove traces older than this many days", example = "7")
        @RequestParam(defaultValue = "7") int olderThanDays) {
        Map<String, Object> result = telemetryService.cleanupOldTraces(olderThanDays);
        return ResponseEntity.ok(result);
    }
}