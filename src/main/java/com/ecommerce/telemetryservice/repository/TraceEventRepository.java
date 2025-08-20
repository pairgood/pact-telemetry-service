package com.ecommerce.telemetryservice.repository;

import com.ecommerce.telemetryservice.model.TraceEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TraceEventRepository extends JpaRepository<TraceEvent, Long> {
    
    List<TraceEvent> findByTraceIdOrderByTimestamp(String traceId);
    
    List<TraceEvent> findByServiceNameOrderByTimestampDesc(String serviceName);
    
    @Query("SELECT DISTINCT te.serviceName FROM TraceEvent te ORDER BY te.serviceName")
    List<String> findDistinctServiceNames();
    
    @Query("SELECT DISTINCT te.operation FROM TraceEvent te WHERE te.serviceName = :serviceName ORDER BY te.operation")
    List<String> findDistinctOperationsByServiceName(@Param("serviceName") String serviceName);
    
    @Query("SELECT DISTINCT te.traceId FROM TraceEvent te")
    List<String> findDistinctTraceIds(PageRequest pageRequest);
    
    @Query("SELECT COUNT(te) FROM TraceEvent te WHERE te.timestamp > :since")
    long countRecentEvents(@Param("since") LocalDateTime since);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TraceEvent te WHERE te.timestamp < :cutoffDate")
    int deleteByTimestampBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    List<TraceEvent> findByStatus(TraceEvent.Status status);
    
    List<TraceEvent> findByServiceNameAndOperation(String serviceName, String operation);
    
    @Query("SELECT te FROM TraceEvent te WHERE te.timestamp BETWEEN :startTime AND :endTime ORDER BY te.timestamp")
    List<TraceEvent> findByTimestampBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}