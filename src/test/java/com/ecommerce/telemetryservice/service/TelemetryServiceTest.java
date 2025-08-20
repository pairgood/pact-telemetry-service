package com.ecommerce.telemetryservice.service;

import com.ecommerce.telemetryservice.dto.TraceEventDto;
import com.ecommerce.telemetryservice.model.TraceEvent;
import com.ecommerce.telemetryservice.repository.TraceEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelemetryServiceTest {

    @Mock
    private TraceEventRepository traceEventRepository;

    @InjectMocks
    private TelemetryService telemetryService;

    private TraceEvent testTraceEvent;
    private TraceEventDto testTraceEventDto;

    @BeforeEach
    void setUp() {
        testTraceEvent = new TraceEvent();
        testTraceEvent.setId(1L);
        testTraceEvent.setTraceId("trace-123");
        testTraceEvent.setSpanId("span-456");
        testTraceEvent.setServiceName("test-service");
        testTraceEvent.setOperation("test_operation");
        testTraceEvent.setTimestamp(LocalDateTime.now());
        testTraceEvent.setStatus(TraceEvent.Status.SUCCESS);
        testTraceEvent.setDurationMs(100L);
        testTraceEvent.setEventType(TraceEvent.EventType.SPAN);

        testTraceEventDto = new TraceEventDto();
        testTraceEventDto.setTraceId("trace-123");
        testTraceEventDto.setSpanId("span-456");
        testTraceEventDto.setServiceName("test-service");
        testTraceEventDto.setOperation("test_operation");
        testTraceEventDto.setTimestamp(LocalDateTime.now());
        testTraceEventDto.setStatus(TraceEvent.Status.SUCCESS);
        testTraceEventDto.setDurationMs(100L);
        testTraceEventDto.setEventType(TraceEvent.EventType.SPAN);
    }

    @Test
    void recordEvent_ShouldSaveAndReturnTraceEvent() {
        // Given
        when(traceEventRepository.save(any(TraceEvent.class))).thenReturn(testTraceEvent);

        // When
        TraceEvent result = telemetryService.recordEvent(testTraceEventDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTraceId()).isEqualTo("trace-123");
        assertThat(result.getSpanId()).isEqualTo("span-456");
        assertThat(result.getServiceName()).isEqualTo("test-service");
        assertThat(result.getOperation()).isEqualTo("test_operation");
        verify(traceEventRepository).save(any(TraceEvent.class));
    }

    @Test
    void recordEvent_WithNullTimestamp_ShouldSetCurrentTime() {
        // Given
        testTraceEventDto.setTimestamp(null);
        when(traceEventRepository.save(any(TraceEvent.class))).thenReturn(testTraceEvent);

        // When
        TraceEvent result = telemetryService.recordEvent(testTraceEventDto);

        // Then
        assertThat(result).isNotNull();
        verify(traceEventRepository).save(argThat(event -> 
            event.getTimestamp() != null));
    }

    @Test
    void recordEvent_WithNullEventType_ShouldSetDefaultEventType() {
        // Given
        testTraceEventDto.setEventType(null);
        when(traceEventRepository.save(any(TraceEvent.class))).thenReturn(testTraceEvent);

        // When
        TraceEvent result = telemetryService.recordEvent(testTraceEventDto);

        // Then
        assertThat(result).isNotNull();
        verify(traceEventRepository).save(argThat(event -> 
            event.getEventType() == TraceEvent.EventType.SPAN));
    }

    @Test
    void recordEvent_WithNullStatus_ShouldSetDefaultStatus() {
        // Given
        testTraceEventDto.setStatus(null);
        when(traceEventRepository.save(any(TraceEvent.class))).thenReturn(testTraceEvent);

        // When
        TraceEvent result = telemetryService.recordEvent(testTraceEventDto);

        // Then
        assertThat(result).isNotNull();
        verify(traceEventRepository).save(argThat(event -> 
            event.getStatus() == TraceEvent.Status.SUCCESS));
    }

    @Test
    void recordEventsBatch_ShouldSaveAllEventsAndReturnList() {
        // Given
        TraceEventDto dto2 = new TraceEventDto();
        dto2.setTraceId("trace-124");
        dto2.setSpanId("span-457");
        dto2.setServiceName("test-service-2");
        dto2.setOperation("test_operation_2");

        TraceEvent event2 = new TraceEvent();
        event2.setId(2L);
        event2.setTraceId("trace-124");
        event2.setSpanId("span-457");
        event2.setServiceName("test-service-2");
        event2.setOperation("test_operation_2");

        List<TraceEventDto> dtos = Arrays.asList(testTraceEventDto, dto2);
        List<TraceEvent> savedEvents = Arrays.asList(testTraceEvent, event2);

        when(traceEventRepository.saveAll(anyList())).thenReturn(savedEvents);

        // When
        List<TraceEvent> result = telemetryService.recordEventsBatch(dtos);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTraceId()).isEqualTo("trace-123");
        assertThat(result.get(1).getTraceId()).isEqualTo("trace-124");
        verify(traceEventRepository).saveAll(anyList());
    }

    @Test
    void getTraceById_ShouldReturnEventsForTrace() {
        // Given
        List<TraceEvent> events = Arrays.asList(testTraceEvent);
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-123")).thenReturn(events);

        // When
        List<TraceEvent> result = telemetryService.getTraceById("trace-123");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTraceId()).isEqualTo("trace-123");
        verify(traceEventRepository).findByTraceIdOrderByTimestamp("trace-123");
    }

    @Test
    void getTraceTimeline_WithEvents_ShouldReturnTimeline() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endTime = LocalDateTime.now();
        
        testTraceEvent.setTimestamp(startTime);
        TraceEvent event2 = new TraceEvent();
        event2.setTraceId("trace-123");
        event2.setSpanId("span-457");
        event2.setServiceName("test-service");
        event2.setOperation("test_operation_2");
        event2.setTimestamp(endTime);

        List<TraceEvent> events = Arrays.asList(testTraceEvent, event2);
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-123")).thenReturn(events);

        // When
        Map<String, Object> result = telemetryService.getTraceTimeline("trace-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("traceId")).isEqualTo("trace-123");
        assertThat(result.get("events")).isInstanceOf(List.class);
        assertThat(result.get("totalDuration")).isInstanceOf(Long.class);
        assertThat(result.get("startTime")).isEqualTo(startTime);
        assertThat(result.get("endTime")).isEqualTo(endTime);
        assertThat(result.get("serviceCount")).isEqualTo(1L);
        verify(traceEventRepository).findByTraceIdOrderByTimestamp("trace-123");
    }

    @Test
    void getTraceTimeline_WithNoEvents_ShouldReturnEmptyTimeline() {
        // Given
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-123")).thenReturn(Collections.emptyList());

        // When
        Map<String, Object> result = telemetryService.getTraceTimeline("trace-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("traceId")).isEqualTo("trace-123");
        assertThat(result.get("events")).isEqualTo(Collections.emptyList());
        assertThat(result.get("totalDuration")).isEqualTo(0);
        verify(traceEventRepository).findByTraceIdOrderByTimestamp("trace-123");
    }

    @Test
    void getTraces_ShouldReturnPaginatedTraces() {
        // Given
        List<String> traceIds = Arrays.asList("trace-123", "trace-124");
        when(traceEventRepository.findDistinctTraceIds(any(PageRequest.class))).thenReturn(traceIds);
        
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-123"))
            .thenReturn(Arrays.asList(testTraceEvent));
        
        TraceEvent event2 = new TraceEvent();
        event2.setTraceId("trace-124");
        event2.setSpanId("span-457");
        event2.setServiceName("test-service-2");
        event2.setOperation("test_operation_2");
        event2.setTimestamp(LocalDateTime.now());
        
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-124"))
            .thenReturn(Arrays.asList(event2));

        PageRequest pageRequest = PageRequest.of(0, 20);

        // When
        Page<Map<String, Object>> result = telemetryService.getTraces(pageRequest, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).get("traceId")).isEqualTo("trace-123");
        assertThat(result.getContent().get(1).get("traceId")).isEqualTo("trace-124");
        verify(traceEventRepository).findDistinctTraceIds(pageRequest);
    }

    @Test
    void getTraces_WithServiceNameFilter_ShouldFilterByService() {
        // Given
        List<String> traceIds = Arrays.asList("trace-123", "trace-124");
        when(traceEventRepository.findDistinctTraceIds(any(PageRequest.class))).thenReturn(traceIds);
        
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-123"))
            .thenReturn(Arrays.asList(testTraceEvent));
        
        TraceEvent event2 = new TraceEvent();
        event2.setTraceId("trace-124");
        event2.setServiceName("other-service");
        when(traceEventRepository.findByTraceIdOrderByTimestamp("trace-124"))
            .thenReturn(Arrays.asList(event2));

        PageRequest pageRequest = PageRequest.of(0, 20);

        // When
        Page<Map<String, Object>> result = telemetryService.getTraces(pageRequest, "test-service", null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).get("traceId")).isEqualTo("trace-123");
        verify(traceEventRepository).findDistinctTraceIds(pageRequest);
    }

    @Test
    void getServices_ShouldReturnDistinctServiceNames() {
        // Given
        List<String> services = Arrays.asList("service-1", "service-2", "service-3");
        when(traceEventRepository.findDistinctServiceNames()).thenReturn(services);

        // When
        List<String> result = telemetryService.getServices();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("service-1", "service-2", "service-3");
        verify(traceEventRepository).findDistinctServiceNames();
    }

    @Test
    void getServiceOperations_ShouldReturnOperationsForService() {
        // Given
        List<String> operations = Arrays.asList("operation-1", "operation-2");
        when(traceEventRepository.findDistinctOperationsByServiceName("test-service")).thenReturn(operations);

        // When
        List<String> result = telemetryService.getServiceOperations("test-service");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("operation-1", "operation-2");
        verify(traceEventRepository).findDistinctOperationsByServiceName("test-service");
    }

    @Test
    void getServiceMetrics_WithEvents_ShouldReturnMetrics() {
        // Given
        TraceEvent event1 = new TraceEvent();
        event1.setServiceName("test-service");
        event1.setOperation("operation-1");
        event1.setDurationMs(100L);
        event1.setStatus(TraceEvent.Status.SUCCESS);

        TraceEvent event2 = new TraceEvent();
        event2.setServiceName("test-service");
        event2.setOperation("operation-2");
        event2.setDurationMs(200L);
        event2.setStatus(TraceEvent.Status.ERROR);

        List<TraceEvent> events = Arrays.asList(event1, event2);
        when(traceEventRepository.findByServiceNameOrderByTimestampDesc("test-service")).thenReturn(events);

        // When
        Map<String, Object> result = telemetryService.getServiceMetrics("test-service");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("serviceName")).isEqualTo("test-service");
        assertThat(result.get("requestCount")).isEqualTo(2L);
        assertThat(result.get("averageDurationMs")).isEqualTo(150L);
        assertThat(result.get("errorCount")).isEqualTo(1L);
        assertThat(result.get("errorRate")).isEqualTo(50.0);
        assertThat(result.get("operationCounts")).isInstanceOf(Map.class);
        verify(traceEventRepository).findByServiceNameOrderByTimestampDesc("test-service");
    }

    @Test
    void getServiceMetrics_WithNoEvents_ShouldReturnEmptyMetrics() {
        // Given
        when(traceEventRepository.findByServiceNameOrderByTimestampDesc("test-service")).thenReturn(Collections.emptyList());

        // When
        Map<String, Object> result = telemetryService.getServiceMetrics("test-service");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("serviceName")).isEqualTo("test-service");
        assertThat(result.get("requestCount")).isEqualTo(0);
        verify(traceEventRepository).findByServiceNameOrderByTimestampDesc("test-service");
    }

    @Test
    void getHealthStatus_ShouldReturnSystemHealth() {
        // Given
        when(traceEventRepository.count()).thenReturn(1000L);
        when(traceEventRepository.countRecentEvents(any(LocalDateTime.class))).thenReturn(50L);
        when(traceEventRepository.findDistinctServiceNames()).thenReturn(Arrays.asList("service-1", "service-2"));

        // When
        Map<String, Object> result = telemetryService.getHealthStatus();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("healthy");
        assertThat(result.get("totalEvents")).isEqualTo(1000L);
        assertThat(result.get("recentEvents")).isEqualTo(50L);
        assertThat(result.get("trackedServices")).isEqualTo(2);
        assertThat(result.get("services")).isInstanceOf(List.class);
        assertThat(result.get("timestamp")).isInstanceOf(LocalDateTime.class);
        verify(traceEventRepository).count();
        verify(traceEventRepository).countRecentEvents(any(LocalDateTime.class));
        verify(traceEventRepository).findDistinctServiceNames();
    }

    @Test
    void cleanupOldTraces_ShouldDeleteOldTracesAndReturnResult() {
        // Given
        when(traceEventRepository.deleteByTimestampBefore(any(LocalDateTime.class))).thenReturn(100);

        // When
        Map<String, Object> result = telemetryService.cleanupOldTraces(7);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("deletedEvents")).isEqualTo(100);
        assertThat(result.get("cutoffDate")).isInstanceOf(LocalDateTime.class);
        assertThat(result.get("message")).isEqualTo("Cleaned up traces older than 7 days");
        verify(traceEventRepository).deleteByTimestampBefore(any(LocalDateTime.class));
    }
}