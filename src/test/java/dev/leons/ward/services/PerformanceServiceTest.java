package dev.leons.ward.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PerformanceService
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    private PerformanceService performanceService;

    @BeforeEach
    void setUp() throws Exception {
        // Reset static fields before each test
        resetStaticFields();
        performanceService = new PerformanceService();
    }

    @Test
    void testRecordStartupStart() {
        // Given
        long beforeTime = System.currentTimeMillis();
        
        // When
        PerformanceService.recordStartupStart();
        
        // Then
        long afterTime = System.currentTimeMillis();
        long startupTime = performanceService.getStartupTime();
        
        // Startup time should be -1 since we haven't recorded completion yet
        assertEquals(-1, startupTime);
    }

    @Test
    void testRecordStartupComplete() throws InterruptedException {
        // Given
        PerformanceService.recordStartupStart();
        Thread.sleep(10); // Small delay to ensure measurable startup time
        
        // When
        performanceService.recordStartupComplete();
        
        // Then
        long startupTime = performanceService.getStartupTime();
        assertTrue(startupTime > 0, "Startup time should be positive");
        assertTrue(startupTime >= 10, "Startup time should be at least 10ms");
    }

    @Test
    void testGetStartupTimeWhenNotRecorded() {
        // When
        long startupTime = performanceService.getStartupTime();
        
        // Then
        assertEquals(-1, startupTime, "Startup time should be -1 when not recorded");
    }

    @Test
    void testGetFormattedStartupTime() throws InterruptedException {
        // Given
        PerformanceService.recordStartupStart();
        Thread.sleep(10);
        performanceService.recordStartupComplete();
        
        // When
        String formattedTime = performanceService.getFormattedStartupTime();
        
        // Then
        assertNotNull(formattedTime);
        assertTrue(formattedTime.contains("ms") || formattedTime.contains("s"));
    }

    @Test
    void testGetFormattedStartupTimeWhenNotAvailable() {
        // When
        String formattedTime = performanceService.getFormattedStartupTime();
        
        // Then
        assertEquals("Startup time not available", formattedTime);
    }

    @Test
    void testGetCurrentJvmMemory() {
        // When
        Map<String, Object> jvmMemory = performanceService.getCurrentJvmMemory();
        
        // Then
        assertNotNull(jvmMemory);
        assertTrue(jvmMemory.containsKey("heapUsed"));
        assertTrue(jvmMemory.containsKey("heapMax"));
        assertTrue(jvmMemory.containsKey("heapCommitted"));
        assertTrue(jvmMemory.containsKey("heapFree"));
        assertTrue(jvmMemory.containsKey("heapUtilizationPercent"));
        assertTrue(jvmMemory.containsKey("heapUsedFormatted"));
        assertTrue(jvmMemory.containsKey("heapMaxFormatted"));
        assertTrue(jvmMemory.containsKey("heapFreeFormatted"));
        
        // Verify data types
        assertTrue(jvmMemory.get("heapUsed") instanceof Long);
        assertTrue(jvmMemory.get("heapMax") instanceof Long);
        assertTrue(jvmMemory.get("heapUtilizationPercent") instanceof Double);
        assertTrue(jvmMemory.get("heapUsedFormatted") instanceof String);
    }

    @Test
    void testGetCurrentSystemMemory() {
        // When
        Map<String, Object> systemMemory = performanceService.getCurrentSystemMemory();
        
        // Then
        assertNotNull(systemMemory);
        assertTrue(systemMemory.containsKey("totalMemory"));
        assertTrue(systemMemory.containsKey("availableMemory"));
        assertTrue(systemMemory.containsKey("usedMemory"));
        assertTrue(systemMemory.containsKey("memoryUtilizationPercent"));
        assertTrue(systemMemory.containsKey("totalMemoryFormatted"));
        assertTrue(systemMemory.containsKey("availableMemoryFormatted"));
        assertTrue(systemMemory.containsKey("usedMemoryFormatted"));
        
        // Verify data types
        assertTrue(systemMemory.get("totalMemory") instanceof Long);
        assertTrue(systemMemory.get("availableMemory") instanceof Long);
        assertTrue(systemMemory.get("usedMemory") instanceof Long);
        assertTrue(systemMemory.get("memoryUtilizationPercent") instanceof Double);
        assertTrue(systemMemory.get("totalMemoryFormatted") instanceof String);
    }

    @Test
    void testGetStartupMemoryWhenNotRecorded() {
        // When
        Map<String, Object> startupJvmMemory = performanceService.getStartupJvmMemory();
        Map<String, Object> startupSystemMemory = performanceService.getStartupSystemMemory();
        
        // Then
        assertNotNull(startupJvmMemory);
        assertNotNull(startupSystemMemory);
        assertTrue(startupJvmMemory.isEmpty());
        assertTrue(startupSystemMemory.isEmpty());
    }

    @Test
    void testGetStartupMemoryWhenRecorded() {
        // Given
        PerformanceService.recordStartupStart();
        performanceService.recordStartupComplete();
        
        // When
        Map<String, Object> startupJvmMemory = performanceService.getStartupJvmMemory();
        Map<String, Object> startupSystemMemory = performanceService.getStartupSystemMemory();
        
        // Then
        assertNotNull(startupJvmMemory);
        assertNotNull(startupSystemMemory);
        assertFalse(startupJvmMemory.isEmpty());
        assertFalse(startupSystemMemory.isEmpty());
    }

    @Test
    void testGetAllMetrics() {
        // Given
        PerformanceService.recordStartupStart();
        performanceService.recordStartupComplete();
        
        // When
        Map<String, Object> metrics = performanceService.getAllMetrics();
        
        // Then
        assertNotNull(metrics);
        assertTrue(metrics.containsKey("startupTime"));
        assertTrue(metrics.containsKey("startupTimeFormatted"));
        assertTrue(metrics.containsKey("currentJvmMemory"));
        assertTrue(metrics.containsKey("currentSystemMemory"));
        assertTrue(metrics.containsKey("startupJvmMemory"));
        assertTrue(metrics.containsKey("startupSystemMemory"));
        assertTrue(metrics.containsKey("customMetrics"));
    }

    @Test
    void testGetPerformanceSummary() {
        // Given
        PerformanceService.recordStartupStart();
        performanceService.recordStartupComplete();
        
        // When
        Map<String, Object> summary = performanceService.getPerformanceSummary();
        
        // Then
        assertNotNull(summary);
        assertTrue(summary.containsKey("startupTime"));
        assertTrue(summary.containsKey("startupTimeFormatted"));
        assertTrue(summary.containsKey("currentJvmMemoryUsage"));
        assertTrue(summary.containsKey("currentJvmMemoryPercent"));
        assertTrue(summary.containsKey("currentSystemMemoryUsage"));
        assertTrue(summary.containsKey("currentSystemMemoryPercent"));
        assertTrue(summary.containsKey("applicationStatus"));
        
        assertEquals("Ready", summary.get("applicationStatus"));
    }

    @Test
    void testRecordCustomMetric() {
        // Given
        String metricName = "test_metric";
        long metricValue = 12345L;
        
        // When
        performanceService.recordCustomMetric(metricName, metricValue);
        
        // Then
        Map<String, Object> metrics = performanceService.getAllMetrics();
        @SuppressWarnings("unchecked")
        Map<String, Long> customMetrics = (Map<String, Long>) metrics.get("customMetrics");
        
        assertTrue(customMetrics.containsKey(metricName));
        assertEquals(metricValue, customMetrics.get(metricName));
    }

    @Test
    void testMeasureExecutionTime() {
        // Given
        AtomicBoolean operationExecuted = new AtomicBoolean(false);
        Runnable operation = () -> {
            try {
                Thread.sleep(10); // Simulate some work
                operationExecuted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        // When
        long executionTime = performanceService.measureExecutionTime(operation, "test_operation");
        
        // Then
        assertTrue(operationExecuted.get(), "Operation should have been executed");
        assertTrue(executionTime >= 10, "Execution time should be at least 10ms");
        
        // Verify custom metric was recorded
        Map<String, Object> metrics = performanceService.getAllMetrics();
        @SuppressWarnings("unchecked")
        Map<String, Long> customMetrics = (Map<String, Long>) metrics.get("customMetrics");
        assertTrue(customMetrics.containsKey("test_operation_execution_time"));
    }

    @Test
    void testMemoryUtilizationCalculation() {
        // When
        Map<String, Object> jvmMemory = performanceService.getCurrentJvmMemory();
        Map<String, Object> systemMemory = performanceService.getCurrentSystemMemory();
        
        // Then
        Double jvmUtilization = (Double) jvmMemory.get("heapUtilizationPercent");
        Double systemUtilization = (Double) systemMemory.get("memoryUtilizationPercent");
        
        assertNotNull(jvmUtilization);
        assertNotNull(systemUtilization);
        assertTrue(jvmUtilization >= 0 && jvmUtilization <= 100, "JVM utilization should be between 0 and 100");
        assertTrue(systemUtilization >= 0 && systemUtilization <= 100, "System utilization should be between 0 and 100");
    }

    @Test
    void testFormattedMemoryValues() {
        // When
        Map<String, Object> jvmMemory = performanceService.getCurrentJvmMemory();
        Map<String, Object> systemMemory = performanceService.getCurrentSystemMemory();
        
        // Then
        String heapUsedFormatted = (String) jvmMemory.get("heapUsedFormatted");
        String totalMemoryFormatted = (String) systemMemory.get("totalMemoryFormatted");
        
        assertNotNull(heapUsedFormatted);
        assertNotNull(totalMemoryFormatted);
        assertTrue(heapUsedFormatted.matches(".*\\d+.*[KMGT]?B"), "Formatted heap memory should contain size unit");
        assertTrue(totalMemoryFormatted.matches(".*\\d+.*[KMGT]?B"), "Formatted total memory should contain size unit");
    }

    /**
     * Helper method to reset static fields using reflection
     */
    private void resetStaticFields() throws Exception {
        Field startupStartTimeField = PerformanceService.class.getDeclaredField("startupStartTime");
        startupStartTimeField.setAccessible(true);
        startupStartTimeField.setLong(null, 0);
        
        Field startupEndTimeField = PerformanceService.class.getDeclaredField("startupEndTime");
        startupEndTimeField.setAccessible(true);
        startupEndTimeField.setLong(null, 0);
        
        Field startupJvmMemoryField = PerformanceService.class.getDeclaredField("startupJvmMemory");
        startupJvmMemoryField.setAccessible(true);
        startupJvmMemoryField.set(null, null);
        
        Field startupSystemMemoryField = PerformanceService.class.getDeclaredField("startupSystemMemory");
        startupSystemMemoryField.setAccessible(true);
        startupSystemMemoryField.set(null, null);
        
        Field customMetricsField = PerformanceService.class.getDeclaredField("customMetrics");
        customMetricsField.setAccessible(true);
        ((Map<?, ?>) customMetricsField.get(null)).clear();
    }
}