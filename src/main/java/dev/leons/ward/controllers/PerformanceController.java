package dev.leons.ward.controllers;

import dev.leons.ward.services.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * PerformanceController provides REST API endpoints for performance monitoring
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    /**
     * Gets application startup time in milliseconds
     *
     * @return ResponseEntity with startup time
     */
    @GetMapping("/startup-time")
    public ResponseEntity<Map<String, Object>> getStartupTime() {
        long startupTime = performanceService.getStartupTime();
        
        Map<String, Object> response = Map.of(
            "startupTimeMs", startupTime,
            "available", startupTime != -1
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets formatted startup time
     *
     * @return ResponseEntity with formatted startup time
     */
    @GetMapping("/startup-time/formatted")
    public ResponseEntity<Map<String, Object>> getFormattedStartupTime() {
        String formattedTime = performanceService.getFormattedStartupTime();
        
        Map<String, Object> response = Map.of(
            "startupTime", formattedTime,
            "startupTimeMs", performanceService.getStartupTime()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets current JVM memory usage
     *
     * @return ResponseEntity with JVM memory metrics
     */
    @GetMapping("/memory/jvm")
    public ResponseEntity<Map<String, Object>> getJvmMemory() {
        Map<String, Object> jvmMemory = performanceService.getCurrentJvmMemory();
        return ResponseEntity.ok(jvmMemory);
    }

    /**
     * Gets current system memory usage
     *
     * @return ResponseEntity with system memory metrics
     */
    @GetMapping("/memory/system")
    public ResponseEntity<Map<String, Object>> getSystemMemory() {
        Map<String, Object> systemMemory = performanceService.getCurrentSystemMemory();
        return ResponseEntity.ok(systemMemory);
    }

    /**
     * Gets all memory metrics (JVM and system)
     *
     * @return ResponseEntity with all memory metrics
     */
    @GetMapping("/memory/all")
    public ResponseEntity<Map<String, Object>> getAllMemoryMetrics() {
        Map<String, Object> response = Map.of(
            "jvmMemory", performanceService.getCurrentJvmMemory(),
            "systemMemory", performanceService.getCurrentSystemMemory(),
            "startupJvmMemory", performanceService.getStartupJvmMemory(),
            "startupSystemMemory", performanceService.getStartupSystemMemory()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets startup memory metrics
     *
     * @return ResponseEntity with startup memory metrics
     */
    @GetMapping("/memory/startup")
    public ResponseEntity<Map<String, Object>> getStartupMemory() {
        Map<String, Object> response = Map.of(
            "jvmMemory", performanceService.getStartupJvmMemory(),
            "systemMemory", performanceService.getStartupSystemMemory()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all performance metrics
     *
     * @return ResponseEntity with all performance metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getAllMetrics() {
        Map<String, Object> metrics = performanceService.getAllMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Gets performance summary
     *
     * @return ResponseEntity with performance summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPerformanceSummary() {
        Map<String, Object> summary = performanceService.getPerformanceSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Gets application health status with performance metrics
     *
     * @return ResponseEntity with health and performance status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthWithPerformance() {
        Map<String, Object> jvmMemory = performanceService.getCurrentJvmMemory();
        Map<String, Object> systemMemory = performanceService.getCurrentSystemMemory();
        
        // Determine health status based on memory usage
        double jvmUtilization = (Double) jvmMemory.get("heapUtilizationPercent");
        double systemUtilization = (Double) systemMemory.get("memoryUtilizationPercent");
        
        String healthStatus = "HEALTHY";
        if (jvmUtilization > 90 || systemUtilization > 95) {
            healthStatus = "CRITICAL";
        } else if (jvmUtilization > 75 || systemUtilization > 85) {
            healthStatus = "WARNING";
        }
        
        Map<String, Object> response = Map.of(
            "status", healthStatus,
            "startupTime", performanceService.getFormattedStartupTime(),
            "jvmMemoryUtilization", jvmUtilization + "%",
            "systemMemoryUtilization", systemUtilization + "%",
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(response);
    }
}