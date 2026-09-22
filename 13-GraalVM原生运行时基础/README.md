# 13 - GraalVM Native Image Basic Demo

## Project Overview

GraalVM Native Image 将 Java 应用编译为原生机器码（独立可执行文件）。

### Core Advantages

| Feature                   | Native Image     | JVM          |
|---------------------------|------------------|--------------|
| Startup Time              | 10-100ms         | 2-10 seconds |
| Memory Usage              | 30-80MB          | 200-500MB    |
| Deployment                | Single binary    | JVM required |
| Serverless                | Instant cold start | Slow warm-up |
| Container Image Size      | ~50MB            | ~200MB+      |

### Tech Stack

- JDK 21
- Spring Boot 3.3.5
- Spring Boot Maven Plugin (AOT support)
- GraalVM native-maven-plugin
- Thymeleaf (template engine)
- Spring Boot Actuator (metrics)

---

## Quick Start

### Run in JVM Development Mode

```bash
mvn spring-boot:run
```

Access: http://localhost:8103/

### Compile to Native Image (requires GraalVM)

See [GRAALVM_SETUP.md](./GRAALVM_SETUP.md) for detailed steps.

Quick compile (with GraalVM installed):

```bash
# Method 1: Using Spring Boot Buildpacks
mvn spring-boot:build-image

# Method 2: Using native-maven-plugin
mvn -Pnative -DskipTests package native:compile

# Method 3: Direct GraalVM native-image command
mvn -DskipTests package
native-image -jar target/graalvm-native-basic-1.0.0.jar
```

---

## Project Structure

```
13-GraalVM-native-basic/
├── pom.xml                           # Maven config with AOT + Native plugin
├── README.md                         # This file
├── GRAALVM_SETUP.md                  # GraalVM installation & compile guide
├── PERFORMANCE.md                    # Performance benchmarks & optimization
├── build-native.sh                   # Linux/Mac build script
├── build-native.bat                  # Windows build script
└── src/main/
    ├── java/com/example/graalvm/
    │   ├── GraalVmBasicApplication.java    # Main app class
    │   ├── entity/
    │   │   ├── User.java                   # User entity
    │   │   └── Order.java                  # Order entity
    │   ├── controller/
    │   │   ├── UserController.java         # User REST API
    │   │   ├── OrderController.java        # Order REST API
    │   │   └── PerformanceController.java  # Metrics API
    │   ├── service/
    │   │   ├── UserService.java            # User business logic
    │   │   └── OrderService.java           # Order business logic
    │   └── aot/
    │       ├── AotConfig.java              # AOT configuration
    │       └── BasicRuntimeHintsRegistrar.java  # RuntimeHints
    └── resources/
        ├── application.properties          # App config (port 8103)
        ├── banner.txt                      # Startup banner
        ├── templates/
        │   └── index.html                  # Performance panel
        └── static/
            ├── css/style.css               # Panel styles
            └── js/perf.js                  # Metrics fetcher
```

---

## AOT Architecture Explained

### What is AOT (Ahead-of-Time)?

Traditional JIT (Just-in-Time) compilation happens at runtime:
1. Bytecode is loaded
2. Hot paths identified
3. Native code generated during execution

AOT compilation moves this to build time:
1. Code analyzed and optimized at build
2. Pre-compiled to native machine code
3. Ready to run immediately on startup

### Spring Boot AOT Processing Flow

```
Source Code → javac → Bytecode
                              ↓
                      Spring AOT Engine
                      (process-aot phase)
                              ↓
                    ┌─────────┴─────────┐
                    ↓                   ↓
            Optimized Code     RuntimeHints JSON
            + Generated AOT    (reflection/proxy/
            classes            resource configs)
                    ↓                   ↓
              Native Image ←────────────┘
              Compiler
                    ↓
            Standalone Executable
            (no JVM needed)
```

### RuntimeHints Registration

GraalVM uses a "closed world" assumption. It need to know at build time:
- Which classes use reflection
- Which resources are loaded dynamically
- Which interfaces use dynamic proxies
- Which classes need serialization support

This information is provided via `RuntimeHintsRegistrar`:

```java
@Configuration
@ImportRuntimeHints(BasicRuntimeHintsRegistrar.class)
public class AotConfig { }
```

---

## API Reference

| Method | Endpoint                | Description              |
|--------|-------------------------|--------------------------|
| GET    | /                       | Performance panel HTML    |
| GET    | /api/users              | List all users            |
| GET    | /api/users/{id}         | Get user by ID            |
| POST   | /api/users              | Create new user           |
| PUT    | /api/users/{id}         | Update user               |
| DELETE | /api/users/{id}         | Delete user               |
| GET    | /api/orders             | List all orders           |
| POST   | /api/orders             | Create new order          |
| GET    | /api/perf/metrics       | Performance metrics JSON  |
| GET    | /api/perf/health        | Health check              |
| GET    | /actuator/health        | Spring Actuator health    |
| GET    | /actuator/info          | Application info          |

---

## Performance Panel

Access the performance panel at: http://localhost:8103/

The panel shows:
- Startup time (ms)
- Heap memory usage (MB)
- Total memory (MB)
- Uptime (ms)
- Native Image vs JVM comparison chart
- AOT compilation flow diagram

---

## Key Folders and Files

- `/aot/` — RuntimeHints registration classes
- `/entity/` — Domain entities (need reflection hints)
- `/controller/` — REST controllers
- `/service/` — Business logic
- `GRAALVM_SETUP.md` — GraalVM installation and native-image compilation
- `PERFORMANCE.md` — Performance metrics and optimization guide

---

## See Also

- [GRAALVM_SETUP.md](./GRAALVM_SETUP.md) — GraalVM install + compile steps
- [PERFORMANCE.md](./PERFORMANCE.md) — Performance benchmarks & tuning
- [Spring AOT Docs](https://docs.spring.io/spring-boot/reference/aot.html)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
