# Stage 1: Build application with Maven AOT processing
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B && mv target/*.jar app.jar

# Stage 2: Build Native Image using GraalVM
FROM ghcr.io/graalvm/graalvm-community:21 AS native-builder
RUN gu install native-image
WORKDIR /build
COPY --from=builder /build/app.jar application.jar
RUN native-image --no-fallback -H:+ReportExceptionStackTraces -H:Name=app -O2 -march=compatibility -jar application.jar

# Stage 3: Create minimal runtime image
FROM ubuntu:22.04 AS runtime
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates tzdata curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=native-builder /build/app /app/app
RUN groupadd -r appuser && useradd -r -g appuser appuser && chown -R appuser:appuser /app
USER appuser
EXPOSE 8104
HEALTHCHECK --interval=30s --timeout=3s --start-period=1s CMD curl -f http://localhost:8104/api/perf/health || exit 1
ENTRYPOINT ["/app/app"]
