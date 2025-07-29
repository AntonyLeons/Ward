package dev.leons.ward.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PerformanceService
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class PerformanceServiceTest {
    
    private PerformanceService performanceService;
    
    @BeforeEach
    void setUp() throws Exception {
        performanceService = new PerformanceService();
        
        // Reset static fields using reflection
        Field startTimeField = PerformanceService.class.getDeclaredField("applicationStartTime");
        startTimeField.setAccessible(true);
        startTimeField.set(null, null);
        
        Field readyTimeField = PerformanceService.class.getDeclaredField("applicationReadyTime");
        readyTimeField.setAccessible(true);
        readyTimeField.set(null, null);
        
        Field startupMemoryField = PerformanceService.class.getDeclaredField("startupMemoryUsage");
        startupMemoryField.setAccessible(true);
        startupMemoryField.set(null, -1L);
    }
    
    @Test
    void testRecordAndGetMetric() {
        // Arrange
        String metricName = "test_operation";
        long expectedValue = 150L;
        
        // Act
        performanceService.recordMetric(metricName, expectedValue);
        Long actualValue = performanceService.getMetric(metricName);
        
        // Assert
        assertEquals(expectedValue, actualValue);
    }
    
    @Test
    void testGetNonExistentMetric() {
        // Act
        Long result = performanceService.getMetric("non_existent");
        
        // Assert
        assertNull(result);
    }
    
    @Test
    void testGetAllMetrics() {
        // Arrange
        performanceService.recordMetric("metric1", 100L);
        performanceService.recordMetric("metric2", 200L);
        
        // Act
        Map<String, Long> allMetrics = performanceService.getAllMetrics();
        
        // Assert
        assertEquals(2, allMetrics.size());
        assertEquals(100L, allMetrics.get("metric1"));
        assertEquals(200L, allMetrics.get("metric2"));
    }
    
    @Test
    void testMeasureExecution() {
        // Arrange
        String operationName = "test_execution";
        
        // Act
        performanceService.measureExecution(operationName, () -> {
            try {
                Thread.sleep(10); // Small delay to measure
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Assert
        Long executionTime = performanceService.getMetric(operationName);
        assertNotNull(executionTime);
        assertTrue(executionTime >= 10); // Should be at least 10ms
    }
    
    @Test
    void testGetFormattedStartupTimeWhenNotAvailable() {
        // Act
        String formattedTime = performanceService.getFormattedStartupTime();
        
        // Assert
        assertEquals("Startup time not available", formattedTime);
    }
    
    @Test
    void testGetStartupTimeMsWhenNotAvailable() {
        // Act
        long startupTime = performanceService.getStartupTimeMs();
        
        // Assert
        assertEquals(-1, startupTime);
    }
    
    @Test
    void testStaticStartupTimeMethods() {
        // Act
        PerformanceService.recordStartTime();
        
        // Small delay to simulate startup time
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        PerformanceService.recordReadyTime();
        
        // Assert
        long startupTime = performanceService.getStartupTimeMs();
        assertTrue(startupTime >= 50);
        
        String formattedTime = performanceService.getFormattedStartupTime();
        assertNotEquals("Startup time not available", formattedTime);
    }
    
    @Test
    void testGetPerformanceSummary() {
        // Arrange
        performanceService.recordMetric("test_metric", 123L);
        
        // Act
        String summary = performanceService.getPerformanceSummary();
        
        // Assert
        assertNotNull(summary);
        assertTrue(summary.contains("Ward Performance Metrics"));
        assertTrue(summary.contains("test_metric: 123 ms"));
        assertTrue(summary.contains("Memory Usage:"));
    }
    
    @Test
    void testGetCurrentJvmMemoryUsage() {
        // Act
        long memoryUsage = PerformanceService.getCurrentJvmMemoryUsage();
        
        // Assert
        assertTrue(memoryUsage > 0, "JVM memory usage should be positive");
    }
    
    @Test
    void testGetCurrentJvmMemoryUsageMB() {
        // Act
        long memoryUsageMB = performanceService.getCurrentJvmMemoryUsageMB();
        
        // Assert
        assertTrue(memoryUsageMB > 0, "JVM memory usage in MB should be positive");
    }
    
    @Test
    void testGetStartupMemoryUsageMBWhenNotAvailable() {
        // Act
        long startupMemoryMB = performanceService.getStartupMemoryUsageMB();
        
        // Assert
        assertEquals(-1, startupMemoryMB, "Startup memory should be -1 when not available");
    }
    
    @Test
    void testGetMemoryIncreaseMBWhenNotAvailable() {
        // Act
        long memoryIncrease = performanceService.getMemoryIncreaseMB();
        
        // Assert
        assertEquals(-1, memoryIncrease, "Memory increase should be -1 when startup memory not available");
    }
    
    @Test
    void testGetSystemMemoryInfo() {
        // Act
        Map<String, Long> memoryInfo = performanceService.getSystemMemoryInfo();
        
        // Assert
        assertNotNull(memoryInfo);
        assertTrue(memoryInfo.containsKey("jvm_heap_used_mb"));
        assertTrue(memoryInfo.containsKey("jvm_heap_max_mb"));
        assertTrue(memoryInfo.containsKey("jvm_heap_committed_mb"));
        assertTrue(memoryInfo.containsKey("jvm_non_heap_used_mb"));
        
        // Verify values are reasonable
        assertTrue(memoryInfo.get("jvm_heap_used_mb") > 0);
        assertTrue(memoryInfo.get("jvm_heap_max_mb") > 0);
    }
    
    @Test
    void testGetMemoryMetrics() {
        // Act
        Map<String, Object> memoryMetrics = performanceService.getMemoryMetrics();
        
        // Assert
        assertNotNull(memoryMetrics);
        assertTrue(memoryMetrics.containsKey("current_jvm_memory_mb"));
        assertTrue(memoryMetrics.containsKey("jvm_heap_used_mb"));
        
        // Current memory should be positive
        assertTrue((Long) memoryMetrics.get("current_jvm_memory_mb") > 0);
    }
    
    @Test
    void testMemoryTrackingWithStartupTime() throws Exception {
        // Act - Record startup time which should also record memory
        PerformanceService.recordStartTime();
        
        // Small delay
        Thread.sleep(10);
        
        PerformanceService.recordReadyTime();
        
        // Assert
        long startupMemoryMB = performanceService.getStartupMemoryUsageMB();
        assertTrue(startupMemoryMB > 0, "Startup memory should be recorded");
        
        long memoryIncrease = performanceService.getMemoryIncreaseMB();
        assertTrue(memoryIncrease >= 0, "Memory increase should be non-negative");
        
        // Test memory metrics include startup data
        Map<String, Object> memoryMetrics = performanceService.getMemoryMetrics();
        assertTrue(memoryMetrics.containsKey("startup_memory_mb"));
        assertTrue(memoryMetrics.containsKey("memory_increase_mb"));
    }
}