# 13 - GraalVM Native Image Advanced Demo

## Project Overview

Production-grade GraalVM Native Image architecture demonstration with full AOT (Ahead-of-Time) processing.

### Key Features

- Complete 5-category RuntimeHints registration
- Full Spring Boot 3.x AOT configuration (spring-boot-starter-aot)
- JVM vs Native comparison panel with real-time metrics
- CI/CD pipeline (Docker multi-stage, GitHub Actions)
- Multiple build paths (Local GraalVM, Paketo Buildpacks, Docker)

### Tech Stack

| Component      | Version  |
|----------------|----------|
| JDK            | 21       |
| Spring Boot    | 3.3.5    |
| GraalVM        | 21       |
| Maven          | 3.9      |

---

## Quick Start

### Run in JVM Development Mode

```bash
mvn spring-boot:run
```

Access:
- Panel: http://localhost:8104/
- Comparison: http://localhost:8104/compare
- Metrics API: http://localhost:8104/api/perf/metrics

### Build Native Image

See [GRAALVM_SETUP.md](./GRAALVM_SETUP.md) for full details.

```bash
# Method 1: Local GraalVM
./ci/build.sh --mode local

# Method 2: Docker multi-stage build
./ci/build.sh --mode docker

# Method 3: Paketo Buildpacks
./ci/build.sh --mode paketo
```

---

## Project Structure

```
13-GraalVM-advanced/
├── pom.xml                           # Full AOT + Native Maven config
├── README.md                         # This file
├── GRAALVM_SETUP.md                  # Complete build guide
├── PAOTUNE.md                        # Performance tuning & benchmarks
├── src/main/
│   ├── java/com/example/graalvm/
│   │   ├── GraalVmAdvancedApplication.java
│   │   ├── entity/
│   │   │   ├── User.java             # 12-field entity
│   │   │   └── Order.java            # 14-field entity with enums
│   │   ├── controller/
│   │   │   ├── HomeController.java   # Thymeleaf views
│   │   │   ├── UserController.java   # REST user endpoints
│   │   │   ├── OrderController.java  # REST order endpoints
│   │   │   └── PerformanceController.java  # Metrics & comparison
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── OrderService.java
│   │   └── aot/
│   │       ├── AotConfig.java        # Aggregator for all hints
│   │       ├── ReflectionHints.java  # Class field/method reflection
│   │       ├── ResourceHints.java    # Templates, configs, static files
│   │       ├── SerializationHints.java # Java serialization
│   │       ├── ProxyHints.java       # JDK Dynamic Proxy
│   │       └── JdkHints.java         # JDK internal management classes
│   └── resources/
│       ├── application.properties    # Port 8104 config
│       ├── banner.txt
│       ├── templates/
│       │   ├── index.html            # Performance panel
│       │   └── compare.html          # Comparison view
│       ├── static/
│       │   ├── css/style.css
│       │   └── js/perf.js, compare.js
│       └── META-INF/native-image/
│           ├── resource-config.json
│           ├── reflect-config.json
│           └── serialization-config.json
├── ci/
│   ├── Dockerfile                    # Multi-stage Docker build
│   ├── Dockerfile.distroless         # Minimal distroless variant
│   ├── build.sh                      # Universal build script
│   ├── build.bat                     # Windows build script
│   └── github-actions.yml            # GitHub Actions CI/CD
```

---

## AOT Hints Registration (5 Categories)

### 1. ReflectionHints (`ReflectionHints.java`)
Handles reflective access:
- Entity classes (User, Order) - all MemberCategory
- Date/Time types (LocalDateTime, LocalDate, Instant)
- Numeric types (BigDecimal)
- Collection types (ArrayList, HashMap, LinkedHashMap)

### 2. ResourceHints (`ResourceHandles.java`)
Ensures resources are included in native binary:
- Thymeleaf templates (`templates/**`)
- Static resources (`static/**`)
- Configuration files (`*.properties`, `*.yml`)
- Banner file
- META-INF resources

### 3. SerializationHandles (`Serializes.java`)
Java native serialization support:
- User, Order entities
- Nested enum types (OrderStatus, PaymentMethod)
- BigDecimal (used in amounts)

### 4. ProxyHints (`ProxyHandles.java`)
Dynamic proxy registration:
- Spring AOP proxy interfaces
- JDK dynamic proxy interfaces
- CGLIB-proxy interfaces (Future-ready)

### 5. JdkHints (`JdkHandles.java`)
JDK internal classes needed in native image:
- java.lang.management classes (for metrics MXBeans)
- Security providers (SecureRandom, SSL)
- Atomic utilities (AtomicLong, AtomicInteger)
- Logging infrastructure

---

## Build Matrix

| Method               | Requirements        | Time      | Output         |
|----------------------|--------------------|-----------|----------------|
| Local GraalVM        | GraalVM installed  | 3-5 min   | Binary file    |
| Docker multi-stage   | Docker             | 10-15 min | Docker image   |
| Paketo Buildpacks   | Docker             | 15-20 min | Docker image   |
| GitHub Actions       | GitHub + secrets   | 10-15 min | Image + binary |

---

## API Endpoints

| Method | Endpoint                  | Description                    |
|--------|---------------------------|-------------------------------|
| GET    | /                         | Performance panel HTML          |
| GET    | /compare                  | JVM vs Native comparison       |
| GET    | /api/users                | List users                      |
| POST   | /api/users                | Create user                     |
| GET    | /api/orders               | List orders                     |
| POST   | /api/orders               | Create order                    |
| GET    | /api/perf/metrics         | Full metrics JSON               |
| GET    | /api/perf/comparison      | Comparison data JSON            |
| GET    | /api/perf/health          | Health check                    |
| GET    | /actuator/health          | Spring health                   |
| GET    | /actuator/info             | App info                        |

---

## Docker Usage

```bash
# Build
docker build -f ci/Dockerfile -t graalvm-native-advanced .

# Run
docker run -p 8104:8104 graalvm-native-advanced

# Distroless (smaller)
docker build -f ci/Dockerfile.distroless -t graalvm-advanced-distroless .
docker run -p 8104:8104 graalvm-advanced-distroless
```

---

## Key Documents

- [GRAALVM_SETUP.md](./GRAALVM_SETUP.md) - GraalVM install and compile guide
- [PERFORMANCE.md](./PERFORMANCE.md) - Performance benchmarks and optimization
- [ci/build.sh](./ci/build.sh) - Universal build script
- [ci/Dockerfile](./ci/Dockerfile) - Multi-stage Docker build
