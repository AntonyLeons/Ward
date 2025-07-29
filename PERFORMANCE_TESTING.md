# Performance Testing Guide for Ward

This guide explains how to use Ward's built-in performance monitoring features to measure and analyze application performance.

## Overview

Ward includes comprehensive performance monitoring capabilities that track:
- Application startup time
- JVM memory usage (heap memory)
- System memory usage (total, available, used RAM)
- Custom performance metrics
- Execution time measurements

## Features

### 1. Startup Time Measurement

Ward automatically measures application startup time from the moment the main method starts until the application is fully initialized.

**Console Output:**
When Ward starts, you'll see performance information in the console:
```
=== Ward Performance Summary ===
Startup Time: 2.5s
JVM Memory Usage: 45.2 MB / 512.0 MB (8.8%)
System Memory Usage: 4.2 GB / 16.0 GB (26.3%)
Application Status: Ready
================================
```

### 2. Memory Usage Tracking

#### JVM Memory
- **Heap Used**: Current heap memory usage
- **Heap Max**: Maximum heap memory available
- **Heap Committed**: Memory committed by the JVM
- **Heap Free**: Available heap memory
- **Heap Utilization**: Percentage of heap memory used

#### System Memory
- **Total Memory**: Total system RAM
- **Available Memory**: Available system RAM
- **Used Memory**: Currently used system RAM
- **Memory Utilization**: Percentage of system memory used

### 3. Custom Metrics

You can record custom performance metrics and measure execution times for specific operations.

## API Endpoints

Ward exposes several REST API endpoints to access performance data:

### Startup Time
```http
GET /api/performance/startup-time
```
Returns the application startup time in milliseconds.

```http
GET /api/performance/startup-time/formatted
```
Returns the formatted startup time (e.g., "2.5s", "1500ms").

### Memory Usage

#### JVM Memory
```http
GET /api/performance/memory/jvm
```
Returns current JVM memory usage:
```json
{
  "heapUsed": 47448064,
  "heapMax": 536870912,
  "heapCommitted": 134217728,
  "heapFree": 489422848,
  "heapUtilizationPercent": 8.84,
  "heapUsedFormatted": "45.2 MB",
  "heapMaxFormatted": "512.0 MB",
  "heapFreeFormatted": "466.8 MB"
}
```

#### System Memory
```http
GET /api/performance/memory/system
```
Returns current system memory usage:
```json
{
  "totalMemory": 17179869184,
  "availableMemory": 12884901888,
  "usedMemory": 4294967296,
  "memoryUtilizationPercent": 25.0,
  "totalMemoryFormatted": "16.0 GB",
  "availableMemoryFormatted": "12.0 GB",
  "usedMemoryFormatted": "4.0 GB"
}
```

#### All Memory Metrics
```http
GET /api/performance/memory/all
```
Returns both current and startup memory usage for JVM and system.

### Performance Summary
```http
GET /api/performance/summary
```
Returns a comprehensive performance summary:
```json
{
  "startupTime": 2500,
  "startupTimeFormatted": "2.5s",
  "currentJvmMemoryUsage": "45.2 MB / 512.0 MB",
  "currentJvmMemoryPercent": 8.84,
  "currentSystemMemoryUsage": "4.0 GB / 16.0 GB",
  "currentSystemMemoryPercent": 25.0,
  "applicationStatus": "Ready"
}
```

### All Metrics
```http
GET /api/performance/metrics
```
Returns all available performance metrics including custom metrics.

### Health Status
```http
GET /api/performance/health
```
Returns application health status based on memory utilization:
```json
{
  "status": "healthy",
  "jvmMemoryUtilization": 8.84,
  "systemMemoryUtilization": 25.0,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Performance Testing Scenarios

### 1. Startup Time Comparison

To compare startup times between different versions or configurations:

1. Start Ward and note the startup time from console output
2. Or call `/api/performance/startup-time/formatted` after startup
3. Record multiple measurements for statistical analysis

### 2. Memory Usage Monitoring

#### Continuous Monitoring
```bash
# Monitor memory usage every 30 seconds
while true; do
  curl -s http://localhost:4000/api/performance/memory/all | jq
  sleep 30
done
```

#### Load Testing Memory Impact
```bash
# Before load test
curl -s http://localhost:4000/api/performance/memory/jvm > before_load.json

# Run your load test here

# After load test
curl -s http://localhost:4000/api/performance/memory/jvm > after_load.json

# Compare results
diff before_load.json after_load.json
```

### 3. Health Monitoring

Set up automated health checks:
```bash
#!/bin/bash
HEALTH_STATUS=$(curl -s http://localhost:4000/api/performance/health | jq -r '.status')
if [ "$HEALTH_STATUS" != "healthy" ]; then
  echo "Warning: Application health status is $HEALTH_STATUS"
  # Send alert or take corrective action
fi
```

## Integration with Monitoring Tools

### Prometheus Integration

You can integrate Ward's performance metrics with Prometheus by creating a custom exporter that calls Ward's API endpoints.

### Grafana Dashboard

Create Grafana dashboards using Ward's performance API endpoints as data sources to visualize:
- Startup time trends
- Memory usage over time
- Application health status
- Custom metrics

## Custom Performance Metrics

If you need to add custom performance tracking in your code:

```java
@Autowired
private PerformanceService performanceService;

// Record a custom metric
performanceService.recordCustomMetric("database_connections", connectionCount);

// Measure execution time
long executionTime = performanceService.measureExecutionTime(() -> {
    // Your operation here
    performDatabaseOperation();
}, "database_operation");
```

## Best Practices

1. **Baseline Measurements**: Always establish baseline performance metrics before making changes
2. **Multiple Measurements**: Take multiple measurements and calculate averages for more reliable data
3. **Environment Consistency**: Ensure consistent testing environments when comparing performance
4. **Memory Monitoring**: Monitor both JVM and system memory to identify bottlenecks
5. **Health Thresholds**: Set up alerts when memory utilization exceeds safe thresholds (e.g., 80%)
6. **Regular Monitoring**: Implement continuous monitoring in production environments

## Troubleshooting

### High Memory Usage

If you notice high memory utilization:

1. Check `/api/performance/memory/jvm` for heap usage details
2. Monitor garbage collection patterns
3. Review application logs for memory-related warnings
4. Consider adjusting JVM heap size parameters

### Slow Startup Times

1. Compare startup times across different environments
2. Check system resource availability during startup
3. Review application configuration for optimization opportunities
4. Monitor startup memory usage patterns

### API Endpoint Issues

If performance API endpoints are not responding:

1. Verify Ward is running and accessible
2. Check application logs for errors
3. Ensure the PerformanceService is properly initialized
4. Verify Spring Boot actuator endpoints are enabled

## Conclusion

Ward's performance monitoring features provide comprehensive insights into application performance, helping you identify bottlenecks, track improvements, and maintain optimal system health. Use these tools regularly to ensure your Ward installation performs efficiently in your environment.