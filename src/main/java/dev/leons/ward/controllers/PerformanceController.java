package dev.leons.ward.controllers;

import dev.leons.ward.services.PerformanceService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.Map;

/**
 * PerformanceController provides REST endpoints for performance metrics
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Controller
public class PerformanceController {
    
    /**
     * Injected PerformanceService object
     * Used for getting performance metrics
     */
    @Inject
    private PerformanceService performanceService;
    
    /**
     * Gets startup time in milliseconds
     *
     * @return startup time in milliseconds
     */
    @Mapping("/api/performance/startup-time")
    public long getStartupTime() {
        return performanceService.getStartupTimeMs();
    }
    
    /**
     * Gets formatted startup time string
     *
     * @return formatted startup time
     */
    @Mapping("/api/performance/startup-time/formatted")
    public String getFormattedStartupTime() {
        return performanceService.getFormattedStartupTime();
    }
    
    /**
     * Gets all performance metrics
     *
     * @return map of all performance metrics
     */
    @Mapping("/api/performance/metrics")
    public Map<String, Long> getAllMetrics() {
        return performanceService.getAllMetrics();
    }
    
    /**
     * Gets performance summary
     *
     * @return formatted performance summary
     */
    @Mapping("/api/performance/summary")
    public String getPerformanceSummary() {
        return performanceService.getPerformanceSummary();
    }
    
    /**
     * Gets current JVM memory usage in MB
     *
     * @return current JVM memory usage in MB
     */
    @Mapping("/api/performance/memory/current")
    public long getCurrentMemoryUsage() {
        return performanceService.getCurrentJvmMemoryUsageMB();
    }
    
    /**
     * Gets startup memory usage in MB
     *
     * @return startup memory usage in MB
     */
    @Mapping("/api/performance/memory/startup")
    public long getStartupMemoryUsage() {
        return performanceService.getStartupMemoryUsageMB();
    }
    
    /**
     * Gets memory increase since startup in MB
     *
     * @return memory increase in MB
     */
    @Mapping("/api/performance/memory/increase")
    public long getMemoryIncrease() {
        return performanceService.getMemoryIncreaseMB();
    }
    
    /**
     * Gets comprehensive memory metrics
     *
     * @return map containing all memory metrics
     */
    @Mapping("/api/performance/memory/all")
    public Map<String, Object> getMemoryMetrics() {
        return performanceService.getMemoryMetrics();
    }
    
    /**
     * Gets system memory information
     *
     * @return map containing system memory metrics
     */
    @Mapping("/api/performance/memory/system")
    public Map<String, Long> getSystemMemoryInfo() {
        return performanceService.getSystemMemoryInfo();
    }
}