package dev.leons.ward.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PerformanceService provides performance monitoring capabilities including
 * startup time tracking and memory usage monitoring
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Slf4j
@Service
public class PerformanceService {

    private static long startupStartTime;
    private static long startupEndTime;
    private static Map<String, Object> startupJvmMemory;
    private static Map<String, Object> startupSystemMemory;
    private static final Map<String, Long> customMetrics = new ConcurrentHashMap<>();
    
    private final SystemInfo systemInfo;
    private final MemoryMXBean memoryMXBean;

    public PerformanceService() {
        this.systemInfo = new SystemInfo();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    /**
     * Records the application startup start time
     * Should be called at the very beginning of main method
     */
    public static void recordStartupStart() {
        startupStartTime = System.currentTimeMillis();
        log.info("Application startup initiated at: {}", startupStartTime);
    }

    /**
     * Records the application startup completion time and captures memory usage
     * Should be called when application is fully initialized
     */
    public void recordStartupComplete() {
        startupEndTime = System.currentTimeMillis();
        startupJvmMemory = getCurrentJvmMemory();
        startupSystemMemory = getCurrentSystemMemory();
        
        long startupTime = getStartupTime();
        log.info("Application startup completed in: {}ms", startupTime);
        
        // Display performance summary
        displayPerformanceSummary();
    }

    /**
     * Gets the application startup time in milliseconds
     *
     * @return startup time in milliseconds, or -1 if not available
     */
    public long getStartupTime() {
        if (startupStartTime == 0 || startupEndTime == 0) {
            return -1;
        }
        return startupEndTime - startupStartTime;
    }

    /**
     * Gets formatted startup time as a human-readable string
     *
     * @return formatted startup time
     */
    public String getFormattedStartupTime() {
        long startupTime = getStartupTime();
        if (startupTime == -1) {
            return "Startup time not available";
        }
        return formatDuration(startupTime);
    }

    /**
     * Gets current JVM memory usage
     *
     * @return Map containing JVM memory metrics
     */
    public Map<String, Object> getCurrentJvmMemory() {
        Map<String, Object> jvmMemory = new HashMap<>();
        
        MemoryUsage heapMemory = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryMXBean.getNonHeapMemoryUsage();
        
        // Heap memory metrics
        jvmMemory.put("heapUsed", heapMemory.getUsed());
        jvmMemory.put("heapMax", heapMemory.getMax());
        jvmMemory.put("heapCommitted", heapMemory.getCommitted());
        jvmMemory.put("heapFree", heapMemory.getMax() - heapMemory.getUsed());
        
        // Non-heap memory metrics
        jvmMemory.put("nonHeapUsed", nonHeapMemory.getUsed());
        jvmMemory.put("nonHeapMax", nonHeapMemory.getMax());
        jvmMemory.put("nonHeapCommitted", nonHeapMemory.getCommitted());
        
        // Calculated metrics
        double heapUtilization = (double) heapMemory.getUsed() / heapMemory.getMax() * 100;
        jvmMemory.put("heapUtilizationPercent", Math.round(heapUtilization * 100.0) / 100.0);
        
        // Formatted values
        jvmMemory.put("heapUsedFormatted", formatBytes(heapMemory.getUsed()));
        jvmMemory.put("heapMaxFormatted", formatBytes(heapMemory.getMax()));
        jvmMemory.put("heapFreeFormatted", formatBytes(heapMemory.getMax() - heapMemory.getUsed()));
        
        return jvmMemory;
    }

    /**
     * Gets current system memory usage
     *
     * @return Map containing system memory metrics
     */
    public Map<String, Object> getCurrentSystemMemory() {
        Map<String, Object> systemMemory = new HashMap<>();
        
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        
        systemMemory.put("totalMemory", totalMemory);
        systemMemory.put("availableMemory", availableMemory);
        systemMemory.put("usedMemory", usedMemory);
        
        // Calculated metrics
        double memoryUtilization = (double) usedMemory / totalMemory * 100;
        systemMemory.put("memoryUtilizationPercent", Math.round(memoryUtilization * 100.0) / 100.0);
        
        // Formatted values
        systemMemory.put("totalMemoryFormatted", formatBytes(totalMemory));
        systemMemory.put("availableMemoryFormatted", formatBytes(availableMemory));
        systemMemory.put("usedMemoryFormatted", formatBytes(usedMemory));
        
        return systemMemory;
    }

    /**
     * Gets JVM memory usage at startup
     *
     * @return Map containing startup JVM memory metrics
     */
    public Map<String, Object> getStartupJvmMemory() {
        return startupJvmMemory != null ? new HashMap<>(startupJvmMemory) : new HashMap<>();
    }

    /**
     * Gets system memory usage at startup
     *
     * @return Map containing startup system memory metrics
     */
    public Map<String, Object> getStartupSystemMemory() {
        return startupSystemMemory != null ? new HashMap<>(startupSystemMemory) : new HashMap<>();
    }

    /**
     * Gets all performance metrics
     *
     * @return Map containing all performance data
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Startup metrics
        metrics.put("startupTime", getStartupTime());
        metrics.put("startupTimeFormatted", getFormattedStartupTime());
        
        // Current memory metrics
        metrics.put("currentJvmMemory", getCurrentJvmMemory());
        metrics.put("currentSystemMemory", getCurrentSystemMemory());
        
        // Startup memory metrics
        metrics.put("startupJvmMemory", getStartupJvmMemory());
        metrics.put("startupSystemMemory", getStartupSystemMemory());
        
        // Custom metrics
        metrics.put("customMetrics", new HashMap<>(customMetrics));
        
        return metrics;
    }

    /**
     * Gets a performance summary
     *
     * @return Map containing performance summary
     */
    public Map<String, Object> getPerformanceSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("startupTime", getStartupTime());
        summary.put("startupTimeFormatted", getFormattedStartupTime());
        
        Map<String, Object> currentJvm = getCurrentJvmMemory();
        Map<String, Object> currentSystem = getCurrentSystemMemory();
        
        summary.put("currentJvmMemoryUsage", currentJvm.get("heapUsedFormatted") + " / " + currentJvm.get("heapMaxFormatted"));
        summary.put("currentJvmMemoryPercent", currentJvm.get("heapUtilizationPercent") + "%");
        summary.put("currentSystemMemoryUsage", currentSystem.get("usedMemoryFormatted") + " / " + currentSystem.get("totalMemoryFormatted"));
        summary.put("currentSystemMemoryPercent", currentSystem.get("memoryUtilizationPercent") + "%");
        summary.put("applicationStatus", "Ready");
        
        return summary;
    }

    /**
     * Records a custom performance metric
     *
     * @param name metric name
     * @param value metric value
     */
    public void recordCustomMetric(String name, long value) {
        customMetrics.put(name, value);
        log.debug("Recorded custom metric: {} = {}", name, value);
    }

    /**
     * Measures execution time of a runnable operation
     *
     * @param operation the operation to measure
     * @param operationName name for logging
     * @return execution time in milliseconds
     */
    public long measureExecutionTime(Runnable operation, String operationName) {
        long startTime = System.currentTimeMillis();
        operation.run();
        long executionTime = System.currentTimeMillis() - startTime;
        
        log.info("Operation '{}' completed in: {}ms", operationName, executionTime);
        recordCustomMetric(operationName + "_execution_time", executionTime);
        
        return executionTime;
    }

    /**
     * Displays performance summary in console
     */
    private void displayPerformanceSummary() {
        Map<String, Object> summary = getPerformanceSummary();
        
        System.out.println("\n=== Ward Performance Summary ===");
        System.out.println("Startup Time: " + summary.get("startupTimeFormatted"));
        System.out.println("JVM Memory Usage: " + summary.get("currentJvmMemoryUsage") + " (" + summary.get("currentJvmMemoryPercent") + ")");
        System.out.println("System Memory Usage: " + summary.get("currentSystemMemoryUsage") + " (" + summary.get("currentSystemMemoryPercent") + ")");
        System.out.println("Application Status: " + summary.get("applicationStatus"));
        System.out.println("================================\n");
    }

    /**
     * Formats duration in milliseconds to human-readable format
     *
     * @param milliseconds duration in milliseconds
     * @return formatted duration string
     */
    private String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.2fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Formats bytes to human-readable format
     *
     * @param bytes number of bytes
     * @return formatted byte string
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
}