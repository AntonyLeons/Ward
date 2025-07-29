# Ward Performance Testing Guide

## Performance Monitoring Overview

Ward now includes comprehensive built-in performance monitoring capabilities that automatically track:
- **Startup Time**: How long the application takes to start up
- **Memory Usage**: JVM and system memory consumption tracking
- **Custom Metrics**: Extensible performance metric recording

## Startup Time Measurement

Ward automatically tracks startup time from the moment `main()` is called until all initialization is complete.

### Features

1. **Automatic Startup Time Tracking**: The application automatically records startup time from the moment `main()` is called until all initialization is complete.

2. **Console Output**: Startup time and memory usage are displayed in the console when the application starts:
   ```
   === Ward Performance Metrics ===
   Startup Time: 393 ms
   
   Memory Usage:
     Startup Memory: 45 MB
     Current JVM Memory: 52 MB
     Memory Increase: 7 MB
     System Memory: 8234/16384 MB
   ```

3. **REST API Endpoints**: Access performance metrics programmatically via REST endpoints:

   **Startup Time Endpoints:**
   - **Get startup time in milliseconds**:
     ```
     GET /api/performance/startup-time
     Response: 393
     ```

   - **Get formatted startup time**:
     ```
     GET /api/performance/startup-time/formatted
     Response: "393 ms"
     ```

   **Memory Usage Endpoints:**
   - **Get current JVM memory usage**:
     ```
     GET /api/performance/memory/current
     Response: 52
     ```

   - **Get startup memory usage**:
     ```
     GET /api/performance/memory/startup
     Response: 45
     ```

   - **Get memory increase since startup**:
     ```
     GET /api/performance/memory/increase
     Response: 7
     ```

   - **Get comprehensive memory metrics**:
     ```
     GET /api/performance/memory/all
     Response: {
       "startup_memory_mb": 45,
       "current_jvm_memory_mb": 52,
       "memory_increase_mb": 7,
       "total_system_memory_mb": 16384,
       "available_system_memory_mb": 8150,
       "used_system_memory_mb": 8234,
       "jvm_heap_used_mb": 52,
       "jvm_heap_max_mb": 4096,
       "jvm_heap_committed_mb": 256,
       "jvm_non_heap_used_mb": 45
     }
     ```

   - **Get system memory information**:
     ```
     GET /api/performance/memory/system
     Response: {
       "total_system_memory_mb": 16384,
       "available_system_memory_mb": 8150,
       "used_system_memory_mb": 8234,
       "jvm_heap_used_mb": 52,
       "jvm_heap_max_mb": 4096,
       "jvm_heap_committed_mb": 256,
       "jvm_non_heap_used_mb": 45
     }
     ```

   **General Performance Endpoints:**
   - **Get all performance metrics**:
     ```
     GET /api/performance/metrics
     Response: {"startup_time_ms": 393}
     ```

   - **Get performance summary**:
     ```
     GET /api/performance/summary
     Response: "=== Ward Performance Metrics ===\nStartup Time: 393 ms\n\nMemory Usage:\n..."
     ```

## Memory Usage Monitoring

Ward automatically tracks memory usage throughout the application lifecycle.

### Features

1. **Startup Memory Tracking**: Records JVM memory usage at application startup
2. **Current Memory Monitoring**: Real-time JVM memory usage tracking
3. **Memory Increase Calculation**: Tracks memory growth since startup
4. **System Memory Information**: Provides system-wide memory statistics using OSHI
5. **Comprehensive Memory Metrics**: Detailed JVM heap and non-heap memory information

### Memory Metrics Explained

- **Startup Memory**: JVM heap memory used when the application started
- **Current JVM Memory**: Current JVM heap memory usage
- **Memory Increase**: Difference between current and startup memory usage
- **Total System Memory**: Total physical memory available on the system
- **Available System Memory**: Currently available system memory
- **Used System Memory**: Currently used system memory
- **JVM Heap Used**: Current heap memory usage
- **JVM Heap Max**: Maximum heap memory available to JVM
- **JVM Heap Committed**: Memory committed by JVM for heap
- **JVM Non-Heap Used**: Non-heap memory usage (method area, code cache, etc.)

### How It Works

1. **PerformanceService**: A comprehensive service class that handles all performance monitoring
2. **Static Timing & Memory Tracking**: Uses static methods to record start/ready times and memory usage across the application lifecycle
3. **OSHI Integration**: Leverages OSHI library for system memory information
4. **JVM Management Beans**: Uses Java Management Extensions (JMX) for JVM memory metrics
5. **Automatic Integration**: Integrated into the main Ward class to automatically track performance without manual intervention

### Usage Examples

#### Running Ward and Viewing Startup Time

```bash
# Build the application
mvn clean package

# Run Ward
java -jar target/ward-2.5.4.jar

# You'll see output like:
# [main] INFO dev.leons.ward.services.PerformanceService - Application startup initiated at: 2025-07-29T22:22:02.902559Z with memory usage: 45 MB
# [main] INFO dev.leons.ward.services.PerformanceService - Application startup completed in: 393 ms, current memory: 52 MB, memory increase: 7 MB
# === Ward Performance Metrics ===
# Startup Time: 393 ms
# 
# Memory Usage:
#   Startup Memory: 45 MB
#   Current JVM Memory: 52 MB
#   Memory Increase: 7 MB
#   System Memory: 8234/16384 MB
```

#### Testing API Endpoints

```bash
# Startup Time Endpoints
curl http://localhost:4000/api/performance/startup-time
curl http://localhost:4000/api/performance/startup-time/formatted

# Memory Usage Endpoints
curl http://localhost:4000/api/performance/memory/current
curl http://localhost:4000/api/performance/memory/startup
curl http://localhost:4000/api/performance/memory/increase
curl http://localhost:4000/api/performance/memory/all
curl http://localhost:4000/api/performance/memory/system

# General Performance Endpoints
curl http://localhost:4000/api/performance/metrics
curl http://localhost:4000/api/performance/summary
```

### Performance Testing Strategies

#### 1. Startup Time Optimization
- Monitor startup time across different environments
- Compare startup times before/after code changes
- Track startup time in CI/CD pipelines

#### 2. Memory Usage Optimization
- Monitor memory consumption patterns
- Track memory leaks by observing memory increase over time
- Compare memory usage across different configurations
- Set up alerts for excessive memory usage

#### 3. Load Testing
- Use tools like JMeter, Artillery, or k6 to test Ward under load
- Monitor performance metrics during load tests
- Test concurrent access to monitoring endpoints
- Monitor memory usage under load conditions

#### 4. Custom Metrics
The PerformanceService supports custom metrics:

```java
@Inject
private PerformanceService performanceService;

// Record a custom metric
performanceService.recordMetric("database_connection_time", 150L);

// Measure execution time of a code block
performanceService.measureExecution("data_processing", () -> {
    // Your code here
});

// Get current memory usage programmatically
long currentMemoryMB = performanceService.getCurrentJvmMemoryUsageMB();

// Get comprehensive memory metrics
Map<String, Object> memoryMetrics = performanceService.getMemoryMetrics();
```

### Testing

Run the performance service tests:

```bash
mvn test -Dtest=PerformanceServiceTest
```

### Typical Performance Metrics

#### Startup Times
- **Development Environment**: 300-500ms
- **Production Environment**: 200-400ms (depending on hardware)
- **Docker Container**: 400-600ms (depending on container resources)

#### Memory Usage
- **Startup Memory**: 30-60 MB (typical JVM startup)
- **Runtime Memory**: 50-100 MB (depending on monitoring load)
- **Memory Increase**: 10-40 MB (during normal operation)
- **System Memory**: Varies by system configuration

### Troubleshooting

1. **Startup time not showing**: Ensure the PerformanceService is properly injected and the application completed startup
2. **Memory metrics not available**: Check that OSHI dependency is properly loaded and SystemInfo is injected
3. **API endpoints not responding**: Verify the application is running on the correct port (default: 4000)
4. **High startup times**: Check for:
   - Network connectivity issues
   - Resource constraints (CPU/Memory)
   - Configuration file loading delays
5. **High memory usage**: Monitor for:
   - Memory leaks in custom code
   - Excessive system monitoring frequency
   - Large configuration files or data structures
6. **Negative memory values**: Indicates measurement timing issues or JVM garbage collection

### Integration with Monitoring Tools

The performance metrics can be integrated with external monitoring tools:

- **Prometheus**: Expose metrics via custom endpoint
- **Grafana**: Create dashboards for startup time trends
- **Application Performance Monitoring (APM)**: Include startup time in APM metrics

This performance measurement system provides a foundation for monitoring Ward's performance and can be extended for more comprehensive performance testing needs.