# GraalVM Native Image Performance Guide

## Performance Metrics Overview

### Key Performance Indicators (KPIs)

| Metric               | Native Image | JVM (HotSpot) | Improvement |
|----------------------|-------------|---------------|-------------|
| Cold Start           | 10-100ms    | 2000-8000ms   | 50-100x     |
| Memory (RSS)         | 30-80MB     | 200-500MB     | 3-6x        |
| Memory (Heap)        | 20-50MB     | 100-300MB     | 3-5x        |
| Throughput (steady)  | ~95%        | 100%          | Slight loss  |
| Binary Size          | 60-120MB    | N/A           | --          |
| Container Image      | 50-100MB    | 200-500MB     | 3-5x        |

### Detailed Metrics

#### 1. Startup Time Breakdown

```
Native Image:
  [0ms]     OS loads executable
  [5-25mm]    Static initializers run
  [25-50mm]   Spring context init (pre-computed)
  [50-100mm]  First request handling ready

JVM:
  [0ms]       JVM process starts
  [500-2000mm] JVM boot + class loading + JIT warmup
  [2000-5000mm] Spring context init
  [5000-8000mm] First request handling ready
```

#### 2. Memory Footprint

```
Native Image (RSS):
  ┌──────────────────────┐
  │ Code: 40-60MB        │
  │ Heap: 15-30MB        │
  │ Stack: 2-5MB         │
  │ Other: 5-10MB        │
  │ Total: 60-105MB      │
  └──────────────────────┘

JVM HotSpot (RSS):
  ┌──────────────────────┐
  │ JVM overhead: 50MB   │
  │ Metaspace: 30-60MB   │
  │ Heap: 128-256MB      │
  │ Thread stacks: 10MB  │
  │ GC structures: 20MB  │
  │ Total: 238-400MB     │
  └──────────────────────┘
```

---

## JVM vs Native Image: Detailed Comparison

### CPU Performance

| Workload         | Native Image | JVM (C2 JIT) |
|------------------|-------------|--------------|
| Short-lived tasks| Native wins | JIT warmup overhead |
| Long-running CPU | -5% to -10% | JIT optimized peak  |
| Memory-bound     | Native wins | Less GC pressure    |
| Startup-heavy    | 50-100x     | --               |

### Garbage Collection

- **Native Image**: Uses serial GC by default (small footprint)
- **JVM**: G1GC/ZGC/Shenandoah (tuned for throughput)

### Throughput

- First 100 requests: Native faster (no warmup)
- After warmup: JVM slightly faster (JIT optimization)
- Memory pressure: Native more consistent (less GC)

---

## Optimization Recommendations

### 1. Reduce Binary Size

```xml
<!-- In pom.xml native-maven-plugin config -->
<buildArgs>
    <!-- Strip debug symbols -->
    <buildArg>-g</buildArg>
    <!-- Strip symbols -->
    <buildArg>-H:-DeleteLocalSymbols</buildArg>
    <!-- Compress -->
    <buildArg>-Ob</buildArg>
</buildArgs>
```

### 2. Memory Optimization

```bash
# Set heap size for native image
export JAVA_TOOL_OPTIONS="-Xmx64m -Xms32m"

# Or at runtime
./app -Xmx64m
```

### 3. Startup Optimization

```xml
<!-- Mark classes for build-time initialization -->
<buildArg>-H:ClassInitialization=com.example.MyClass:build_time</buildArg>
```

### 4. Reflection Minimization

```java
// Instead of full member registration, be specific:
hints.reflection()
    .registerType(User.class,
        MemberCategory.DECLARED_FIELDS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
```

---

## Serverless Use Case Performance

### AWS Lambda / Azure Functions / Google Cloud Functions

| Metric              | Native Image   | JVM             |
|---------------------|---------------|-----------------|
| Cold Start          | 50-200ms      | 3000-10000ms    |
| Warm Start          | Instant       | Instant         |
| Max Memory          | 64-128MB      | 512-1024MB      |
| Runtime Cost        | Lower         | Higher          |
| Timeout Safe        | Yes           | Risk on cold    |

### Kubernetes Pod Scaling

| Scenario         | Native Image   | JVM             |
|-----------------|---------------|-----------------|
| Pod start time  | 0.1-0.5s      | 5-15s           |
| Horizontal scale | Instant      | Slow initial    |
| Resource tuning | Predictable   | Needs buffer    |
| Node density    | Higher        | Lower           |

---

## Monitoring in this Project

### Performance Panel (Web UI)

Access: http://localhost:8103/

Shows real-time:
- Startup time
- Heap usage
- Total memory
- Uptime

### Metrics API

```bash
# Get all performance metrics
curl http://localhost:8103/api/perf/metrics

# Response:
{
  "startup": {
    "startupTimeMs": 45,
    "jvmStartTimeMs": 1709123456789,
    "uptimeMs": 30000
  },
  "memory": {
    "heapUsedMB": 18.5,
    "heapMaxMB": 64.0,
    "totalMemoryMB": 52.3,
    "maxMemoryMB": 128.0
  },
  "runtime": {
    "isNativeImage": false,
    "javaVersion": "21.0.2",
    "availableProcessors": 8
  }
}
```

---

## Compilation Performance

### Compile Time

| Mode            | Time      | Output Size |
|-----------------|----------|-------------|
| AOT Processing  | 30-60s   | --          |
| Native Compile  | 2-5 min  | 80-150MB    |
| JVM Package     | 5-10s    | 20-50MB     |

### CI/CD Optimization

1. Cache Maven dependencies between builds
2. Use GraalVM base Docker image (no install needed)
3. Parallel compile or use build matrix
4. Pre-warm build machines with compilation cache
