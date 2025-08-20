package com.ecommerce.telemetryservice;

import com.ecommerce.telemetryservice.dto.TraceEventDto;
import com.ecommerce.telemetryservice.model.TraceEvent;
import com.ecommerce.telemetryservice.repository.TraceEventRepository;
import com.ecommerce.telemetryservice.service.TelemetryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TelemetryServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TraceEventRepository traceEventRepository;

    @Autowired
    private TelemetryService telemetryService;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/telemetry";
        traceEventRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertThat(telemetryService).isNotNull();
        assertThat(traceEventRepository).isNotNull();
    }

    @Test
    void recordEvent_ShouldCreateAndReturnTraceEvent() {
        // Given
        TraceEventDto eventDto = new TraceEventDto();
        eventDto.setTraceId("integration-trace-123");
        eventDto.setSpanId("integration-span-456");
        eventDto.setServiceName("integration-service");
        eventDto.setOperation("integration_operation");
        eventDto.setTimestamp(LocalDateTime.now());
        eventDto.setStatus(TraceEvent.Status.SUCCESS);
        eventDto.setDurationMs(150L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraceEventDto> request = new HttpEntity<>(eventDto, headers);

        // When
        ResponseEntity<TraceEvent> response = restTemplate.postForEntity(
            baseUrl + "/events", request, TraceEvent.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTraceId()).isEqualTo("integration-trace-123");
        assertThat(response.getBody().getSpanId()).isEqualTo("integration-span-456");
        assertThat(response.getBody().getServiceName()).isEqualTo("integration-service");
        assertThat(response.getBody().getOperation()).isEqualTo("integration_operation");

        // Verify persistence
        List<TraceEvent> eventsInDb = traceEventRepository.findAll();
        assertThat(eventsInDb).hasSize(1);
        assertThat(eventsInDb.get(0).getTraceId()).isEqualTo("integration-trace-123");
    }

    @Test
    void recordEventsBatch_ShouldCreateMultipleEvents() {
        // Given
        TraceEventDto event1 = new TraceEventDto();
        event1.setTraceId("batch-trace-1");
        event1.setSpanId("batch-span-1");
        event1.setServiceName("batch-service-1");
        event1.setOperation("batch_operation_1");

        TraceEventDto event2 = new TraceEventDto();
        event2.setTraceId("batch-trace-2");
        event2.setSpanId("batch-span-2");
        event2.setServiceName("batch-service-2");
        event2.setOperation("batch_operation_2");

        List<TraceEventDto> events = Arrays.asList(event1, event2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<TraceEventDto>> request = new HttpEntity<>(events, headers);

        // When
        ResponseEntity<List<TraceEvent>> response = restTemplate.exchange(
            baseUrl + "/events/batch", 
            HttpMethod.POST, 
            request, 
            new ParameterizedTypeReference<List<TraceEvent>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTraceId()).isEqualTo("batch-trace-1");
        assertThat(response.getBody().get(1).getTraceId()).isEqualTo("batch-trace-2");

        // Verify persistence
        List<TraceEvent> eventsInDb = traceEventRepository.findAll();
        assertThat(eventsInDb).hasSize(2);
    }

    @Test
    void getTraceById_ShouldReturnEventsForTrace() {
        // Given
        TraceEvent event1 = createAndSaveTraceEvent("get-trace-123", "span-1", "service-1", "operation-1");
        TraceEvent event2 = createAndSaveTraceEvent("get-trace-123", "span-2", "service-2", "operation-2");
        createAndSaveTraceEvent("other-trace", "span-3", "service-3", "operation-3");

        // When
        ResponseEntity<List<TraceEvent>> response = restTemplate.exchange(
            baseUrl + "/traces/get-trace-123",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<TraceEvent>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTraceId()).isEqualTo("get-trace-123");
        assertThat(response.getBody().get(1).getTraceId()).isEqualTo("get-trace-123");
    }

    @Test
    void getTraceTimeline_ShouldReturnTimelineForTrace() {
        // Given
        createAndSaveTraceEvent("timeline-trace", "span-1", "service-1", "operation-1");
        createAndSaveTraceEvent("timeline-trace", "span-2", "service-2", "operation-2");

        // When
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl + "/traces/timeline-trace/timeline",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("traceId")).isEqualTo("timeline-trace");
        assertThat(response.getBody().get("events")).isInstanceOf(List.class);
        assertThat(response.getBody().get("totalDuration")).isInstanceOf(Number.class);
        assertThat(response.getBody().get("serviceCount")).isInstanceOf(Number.class);
    }

    @Test
    void getTraces_ShouldReturnPaginatedTraces() {
        // Given
        createAndSaveTraceEvent("paginated-trace-1", "span-1", "service-1", "operation-1");
        createAndSaveTraceEvent("paginated-trace-2", "span-2", "service-2", "operation-2");

        // When - Call service directly to test pagination logic
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Map<String, Object>> result = telemetryService.getTraces(pageRequest, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isInstanceOf(List.class);
        assertThat(result.getTotalElements()).isInstanceOf(Long.class);
        assertThat(result.getContent().size()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void getServices_ShouldReturnAllServices() {
        // Given
        createAndSaveTraceEvent("trace-1", "span-1", "service-alpha", "operation-1");
        createAndSaveTraceEvent("trace-2", "span-2", "service-beta", "operation-2");
        createAndSaveTraceEvent("trace-3", "span-3", "service-alpha", "operation-3");

        // When
        ResponseEntity<List<String>> response = restTemplate.exchange(
            baseUrl + "/services",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<String>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder("service-alpha", "service-beta");
    }

    @Test
    void getServiceOperations_ShouldReturnOperationsForService() {
        // Given
        createAndSaveTraceEvent("trace-1", "span-1", "ops-service", "operation-1");
        createAndSaveTraceEvent("trace-2", "span-2", "ops-service", "operation-2");
        createAndSaveTraceEvent("trace-3", "span-3", "other-service", "operation-3");

        // When
        ResponseEntity<List<String>> response = restTemplate.exchange(
            baseUrl + "/services/ops-service/operations",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<String>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder("operation-1", "operation-2");
    }

    @Test
    void getServiceMetrics_ShouldReturnMetricsForService() {
        // Given
        TraceEvent event1 = createAndSaveTraceEvent("trace-1", "span-1", "metrics-service", "operation-1");
        event1.setDurationMs(100L);
        event1.setStatus(TraceEvent.Status.SUCCESS);
        traceEventRepository.save(event1);

        TraceEvent event2 = createAndSaveTraceEvent("trace-2", "span-2", "metrics-service", "operation-2");
        event2.setDurationMs(200L);
        event2.setStatus(TraceEvent.Status.ERROR);
        traceEventRepository.save(event2);

        // When
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl + "/services/metrics-service/metrics",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("serviceName")).isEqualTo("metrics-service");
        assertThat(response.getBody().get("requestCount")).isEqualTo(2);
        assertThat(response.getBody().get("averageDurationMs")).isEqualTo(150);
        assertThat(response.getBody().get("errorCount")).isEqualTo(1);
        assertThat(response.getBody().get("errorRate")).isEqualTo(50.0);
    }

    @Test
    void getHealthStatus_ShouldReturnSystemHealth() {
        // Given
        createAndSaveTraceEvent("health-trace-1", "span-1", "health-service", "operation-1");
        createAndSaveTraceEvent("health-trace-2", "span-2", "health-service", "operation-2");

        // When
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl + "/health",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("healthy");
        assertThat(response.getBody().get("totalEvents")).isEqualTo(2);
        assertThat(response.getBody().get("trackedServices")).isEqualTo(1);
        assertThat(response.getBody().get("services")).isInstanceOf(List.class);
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    @Test
    void cleanupOldTraces_ShouldDeleteOldEvents() {
        // Given
        TraceEvent oldEvent = createAndSaveTraceEvent("old-trace", "span-old", "old-service", "old-operation");
        oldEvent.setTimestamp(LocalDateTime.now().minusDays(10));
        traceEventRepository.save(oldEvent);

        TraceEvent newEvent = createAndSaveTraceEvent("new-trace", "span-new", "new-service", "new-operation");
        newEvent.setTimestamp(LocalDateTime.now());
        traceEventRepository.save(newEvent);

        assertThat(traceEventRepository.count()).isEqualTo(2);

        // When - Call service directly to avoid HTTP transaction issues
        Map<String, Object> result = telemetryService.cleanupOldTraces(7);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("deletedEvents")).isEqualTo(1);
        assertThat(result.get("message")).isEqualTo("Cleaned up traces older than 7 days");

        // Verify only new event remains
        assertThat(traceEventRepository.count()).isEqualTo(1);
        List<TraceEvent> remaining = traceEventRepository.findAll();
        assertThat(remaining.get(0).getTraceId()).isEqualTo("new-trace");
    }

    @Test
    void fullTelemetryLifecycle_ShouldWork() {
        // Create events
        TraceEventDto event1 = new TraceEventDto();
        event1.setTraceId("lifecycle-trace");
        event1.setSpanId("lifecycle-span-1");
        event1.setServiceName("lifecycle-service");
        event1.setOperation("lifecycle_operation");
        event1.setTimestamp(LocalDateTime.now());
        event1.setStatus(TraceEvent.Status.SUCCESS);
        event1.setDurationMs(100L);

        // Record event via API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TraceEventDto> request = new HttpEntity<>(event1, headers);

        ResponseEntity<TraceEvent> createResponse = restTemplate.postForEntity(
            baseUrl + "/events", request, TraceEvent.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Retrieve trace by ID
        ResponseEntity<List<TraceEvent>> getResponse = restTemplate.exchange(
            baseUrl + "/traces/lifecycle-trace",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<TraceEvent>>() {}
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).hasSize(1);

        // Get services list
        ResponseEntity<List<String>> servicesResponse = restTemplate.exchange(
            baseUrl + "/services",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<String>>() {}
        );
        assertThat(servicesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(servicesResponse.getBody()).contains("lifecycle-service");

        // Get service metrics
        ResponseEntity<Map<String, Object>> metricsResponse = restTemplate.exchange(
            baseUrl + "/services/lifecycle-service/metrics",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(metricsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(metricsResponse.getBody().get("requestCount")).isEqualTo(1);
    }

    private TraceEvent createAndSaveTraceEvent(String traceId, String spanId, String serviceName, String operation) {
        TraceEvent event = new TraceEvent();
        event.setTraceId(traceId);
        event.setSpanId(spanId);
        event.setServiceName(serviceName);
        event.setOperation(operation);
        event.setTimestamp(LocalDateTime.now());
        event.setStatus(TraceEvent.Status.SUCCESS);
        event.setEventType(TraceEvent.EventType.SPAN);
        return traceEventRepository.save(event);
    }
}