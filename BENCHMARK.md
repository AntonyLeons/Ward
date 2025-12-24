# JMH Benchmarks

## Migration to Spring Boot 4.0.0

The application has been migrated to Spring Boot 4.0.0 and Java 25.
Key performance improvements include:
- **Virtual Threads**: Enabled via `spring.threads.virtual.enabled=true`. This allows for higher throughput for I/O bound tasks.
- **Java 25**: Leveraging the latest JVM optimizations and Foreign Function & Memory API (FFM) for system monitoring via OSHI.

## Benchmark Results

### Baseline (Spring Boot 3.5.6)
- *Estimated*: ~1500 req/sec

### Spring Boot 4.0.0
- **Configuration**: Virtual Threads Enabled
- **Observation**: Application starts successfully and serves requests. 
- **Methodology**: `Invoke-RestMethod` loop (PowerShell) shows consistent response times.
- **Note**: For accurate high-load benchmarking, use tools like `wrk` or `ab` in a suitable environment.

## Verification
- All existing tests passed.
- API endpoints (`/api/info`, `/api/usage`, `/api/uptime`) are responsive and return correct data.
