package dev.leons.ward.services;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * PerformanceService provides performance monitoring capabilities
 * including startup time measurement and other performance metrics
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Slf4j
@Component
public class PerformanceService {
    
    private static Instant applicationStartTime;
    private static Instant applicationReadyTime;
    private static long startupMemoryUsage = -1;
    private final Map<String, Long> performanceMetrics = new ConcurrentHashMap<>();
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    
    @Inject
    private SystemInfo systemInfo;
    
    /**
     * Records the application start time and initial memory usage
     * Should be called at the very beginning of application startup
     */
    public static void recordStartTime() {
        applicationStartTime = Instant.now();
        startupMemoryUsage = getCurrentJvmMemoryUsage();
        log.info("Application startup initiated at: {} with memory usage: {} MB", 
                applicationStartTime, startupMemoryUsage / (1024 * 1024));
    }
    
    /**
     * Records when the application is fully ready
     * Should be called when all initialization is complete
     */
    public static void recordReadyTime() {
        applicationReadyTime = Instant.now();
        if (applicationStartTime != null) {
            Duration startupDuration = Duration.between(applicationStartTime, applicationReadyTime);
            long currentMemory = getCurrentJvmMemoryUsage();
            long memoryIncrease = startupMemoryUsage != -1 ? currentMemory - startupMemoryUsage : -1;
            log.info("Application startup completed in: {} ms, current memory: {} MB, memory increase: {} MB", 
                    startupDuration.toMillis(), 
                    currentMemory / (1024 * 1024),
                    memoryIncrease != -1 ? memoryIncrease / (1024 * 1024) : "unknown");
        }
    }
    
    /**
     * Gets the total startup time in milliseconds
     *
     * @return startup time in milliseconds, or -1 if not available
     */
    public long getStartupTimeMs() {
        if (applicationStartTime != null && applicationReadyTime != null) {
            return Duration.between(applicationStartTime, applicationReadyTime).toMillis();
        }
        return -1;
    }
    
    /**
     * Gets the startup time as a formatted string
     *
     * @return formatted startup time string
     */
    public String getFormattedStartupTime() {
        long startupMs = getStartupTimeMs();
        if (startupMs == -1) {
            return "Startup time not available";
        }
        
        if (startupMs < 1000) {
            return startupMs + " ms";
        } else {
            return String.format("%.2f seconds", startupMs / 1000.0);
        }
    }
    
    /**
     * Records a custom performance metric
     *
     * @param metricName name of the metric
     * @param value value in milliseconds
     */
    public void recordMetric(String metricName, long value) {
        performanceMetrics.put(metricName, value);
        log.debug("Performance metric recorded: {} = {} ms", metricName, value);
    }
    
    /**
     * Gets a specific performance metric
     *
     * @param metricName name of the metric
     * @return metric value in milliseconds, or null if not found
     */
    public Long getMetric(String metricName) {
        return performanceMetrics.get(metricName);
    }
    
    /**
     * Gets all recorded performance metrics
     *
     * @return map of all metrics
     */
    public Map<String, Long> getAllMetrics() {
        Map<String, Long> allMetrics = new ConcurrentHashMap<>(performanceMetrics);
        
        // Add startup time to metrics if available
        long startupTime = getStartupTimeMs();
        if (startupTime != -1) {
            allMetrics.put("startup_time_ms", startupTime);
        }
        
        return allMetrics;
    }
    
    /**
     * Measures execution time of a code block
     *
     * @param operation name of the operation being measured
     * @param runnable the code to execute and measure
     */
    public void measureExecution(String operation, Runnable runnable) {
        Instant start = Instant.now();
        try {
            runnable.run();
        } finally {
            Instant end = Instant.now();
            long duration = Duration.between(start, end).toMillis();
            recordMetric(operation, duration);
        }
    }
    
    /**
     * Gets current JVM memory usage in bytes
     *
     * @return current JVM memory usage in bytes
     */
    public static long getCurrentJvmMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return heapUsage.getUsed();
    }
    
    /**
     * Gets current JVM memory usage in MB
     *
     * @return current JVM memory usage in MB
     */
    public long getCurrentJvmMemoryUsageMB() {
        return getCurrentJvmMemoryUsage() / (1024 * 1024);
    }
    
    /**
     * Gets JVM memory usage at startup in MB
     *
     * @return startup memory usage in MB, or -1 if not available
     */
    public long getStartupMemoryUsageMB() {
        return startupMemoryUsage != -1 ? startupMemoryUsage / (1024 * 1024) : -1;
    }
    
    /**
     * Gets memory increase since startup in MB
     *
     * @return memory increase in MB, or -1 if not available
     */
    public long getMemoryIncreaseMB() {
        if (startupMemoryUsage == -1) {
            return -1;
        }
        long currentMemory = getCurrentJvmMemoryUsage();
        return (currentMemory - startupMemoryUsage) / (1024 * 1024);
    }
    
    /**
     * Gets system memory information
     *
     * @return map containing system memory metrics
     */
    public Map<String, Long> getSystemMemoryInfo() {
        Map<String, Long> memoryInfo = new ConcurrentHashMap<>();
        
        if (systemInfo != null) {
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            memoryInfo.put("total_system_memory_mb", memory.getTotal() / (1024 * 1024));
            memoryInfo.put("available_system_memory_mb", memory.getAvailable() / (1024 * 1024));
            memoryInfo.put("used_system_memory_mb", (memory.getTotal() - memory.getAvailable()) / (1024 * 1024));
        }
        
        // JVM Memory info
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        memoryInfo.put("jvm_heap_used_mb", heapUsage.getUsed() / (1024 * 1024));
        memoryInfo.put("jvm_heap_max_mb", heapUsage.getMax() / (1024 * 1024));
        memoryInfo.put("jvm_heap_committed_mb", heapUsage.getCommitted() / (1024 * 1024));
        memoryInfo.put("jvm_non_heap_used_mb", nonHeapUsage.getUsed() / (1024 * 1024));
        
        return memoryInfo;
    }
    
    /**
     * Gets comprehensive memory metrics including startup and current usage
     *
     * @return map containing all memory metrics
     */
    public Map<String, Object> getMemoryMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        // Startup memory metrics
        long startupMB = getStartupMemoryUsageMB();
        if (startupMB != -1) {
            metrics.put("startup_memory_mb", startupMB);
        }
        
        // Current memory metrics
        metrics.put("current_jvm_memory_mb", getCurrentJvmMemoryUsageMB());
        
        // Memory increase
        long increase = getMemoryIncreaseMB();
        if (increase != -1) {
            metrics.put("memory_increase_mb", increase);
        }
        
        // System memory info
        metrics.putAll(getSystemMemoryInfo());
        
        return metrics;
    }
    
    /**
     * Gets performance summary as a formatted string
     *
     * @return formatted performance summary
     */
    public String getPerformanceSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Ward Performance Metrics ===\n");
        summary.append("Startup Time: ").append(getFormattedStartupTime()).append("\n");
        
        // Memory information
        summary.append("\nMemory Usage:\n");
        long startupMB = getStartupMemoryUsageMB();
        if (startupMB != -1) {
            summary.append("  Startup Memory: ").append(startupMB).append(" MB\n");
        }
        summary.append("  Current JVM Memory: ").append(getCurrentJvmMemoryUsageMB()).append(" MB\n");
        
        long increase = getMemoryIncreaseMB();
        if (increase != -1) {
            summary.append("  Memory Increase: ").append(increase).append(" MB\n");
        }
        
        // System memory if available
        if (systemInfo != null) {
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            long totalMB = memory.getTotal() / (1024 * 1024);
            long usedMB = (memory.getTotal() - memory.getAvailable()) / (1024 * 1024);
            summary.append("  System Memory: ").append(usedMB).append("/").append(totalMB).append(" MB\n");
        }
        
        if (!performanceMetrics.isEmpty()) {
            summary.append("\nOther Metrics:\n");
            performanceMetrics.forEach((key, value) -> 
                summary.append("  ").append(key).append(": ").append(value).append(" ms\n")
            );
        }
        
        return summary.toString();
    }
}