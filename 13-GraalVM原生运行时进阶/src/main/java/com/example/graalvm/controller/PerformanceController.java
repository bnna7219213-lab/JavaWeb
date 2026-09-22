package com.example.graalvm.controller;

import com.example.graalvm.GraalVmAdvancedApplication;
import com.example.graalvm.service.OrderService;
import com.example.graalvm.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.*;
import java.time.Instant;
import java.util.*;

/**
 * Advanced Performance Controller
 *
 * Provides comprehensive performance metrics and JVM vs Native comparison data.
 */
@RestController
@RequestMapping("/api/perf")
public class PerformanceController {

    private static final long START_NANO = System.nanoTime();
    private volatile long readyNanos;

    @Value("${spring.application.name:unknown}")
    private String appName;

    private final UserService userService;
    private final OrderService orderService;

    public PerformanceController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        this.readyNanos = System.nanoTime();
    }

    /**
     * Full performance metrics
     */
    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        Runtime rt = Runtime.getRuntime();

        // Startup
        long ready = readyNanos > 0 ? readyNanos : System.nanoTime();
        Map<String, Object> startup = new LinkedHashMap<>();
        startup.put("startupTimeMs", (ready - START_NANO) / 1_000_000.0);
        startup.put("jvmStartTimeMs", ManagementFactory.getRuntimeMXBean().getStartTime());
        startup.put("uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
        metrics.put("startup", startup);

        // Memory
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("heapUsedMB", mem.getHeapMemoryUsage().getUsed() / 1024.0 / 1024.0);
        memory.put("heapCommittedMB", mem.getHeapMemoryUsage().getCommitted() / 1024.0 / 1024.0);
        memory.put("heapMaxMB", mem.getHeapMemoryUsage().getMax() / 1024.0 / 1024.0);
        memory.put("nonHeapUsedMB", mem.getNonHeapMemoryUsage().getUsed() / 1024.0 / 1024.0);
        memory.put("freeMemoryMB", rt.freeMemory() / 1024.0 / 1024.0);
        memory.put("totalMemoryMB", rt.totalMemory() / 1024.0 / 1024.0);
        memory.put("maxMemoryMB", rt.maxMemory() / 1024.0 / 1024.0);

        List<Map<String, Object>> pools = new ArrayList<>();
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBean()) {
            Map<String, Object> poolInfo = new LinkedHashMap<>();
            poolInfo.put("name", pool.getName());
            poolInfo.put("type", pool.getType().toString());
            poolInfo.put("usedMB", pool.getUsage().getUsed() / 1024.0 / 1024.0);
            poolInfo.put("maxMB", pool.getUsage().getMax() / 1024.0 / 1024.0);
            pools.add(poolInfo);
        }
        memory.put("pools", pools);
        metrics.put("memory", memory);

        // GC
        List<Map<String, Object>> gcList = new ArrayList<>();
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            Map<String, Object> gcInfo = new LinkedHashMap<>();
            gcInfo.put("name", gc.getName());
            gcInfo.put("collectionCount", gc.getCollectionCount());
            gcInfo.put("collectionTimeMs", gc.getCollectionTime());
            gcList.add(gcInfo);
        }
        metrics.put("gc", gcList);

        // Threads
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threads = new LinkedHashMap<>();
        threads.put("threadCount", threadMXBean.getThreadCount());
        threads.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        threads.put("daemonThreadCount", threadMXBean.getDaemonThreadCount());
        metrics.put("threads", threads);

        // Runtime
        Map<String, Object> runtimeInfo = new LinkedHashMap<>();
        runtimeInfo.put("appName", appName);
        runtimeInfo.put("javaVersion", System.getProperty("java.version"));
        runtimeInfo.put("javaVendor", System.getProperty("java.vendor"));
        runtimeInfo.put("osName", System.getProperty("os.name"));
        runtimeInfo.put("osArch", System.getProperty("os.arch"));
        runtimeInfo.put("availableProcessors", rt.availableProcessors());
        runtimeInfo.put("isNativeImage", GraalVmAdvancedApplication.isNativeImage());
        runtimeInfo.put("runtimeMode", GraalVmAdvancedApplication.detectRuntimeMode());
        runtimeInfo.put("timestamp", Instant.now().toString());
        metrics.put("runtime", runtimeInfo);

        // App stats
        Map<String, Object> appStats = new LinkedHashMap<>();
        appStats.put("userCount", userService.count());
        appStats.put("orderCount", orderService.count());
        metrics.put("appStats", appStats);

        return metrics;
    }

    /**
     * JVM vs Native comparison data
     */
    @GetMapping("/comparison")
    public Map<String, Object> getComparison() {
        Map<String, Object> comparison = new LinkedHashMap<>();

        Map<String, Object> startup = new LinkedHashMap<>();
        startup.put("jvm", Map.of(
                "typical", "2000-8000ms",
                "springBootAlone", "1000-3000ms",
                "withLibs", "3000-8000ms",
                "serverless", "UNACCEPTABLE"
        ));
        startup.put("native", Map.of(
                "typical", "10-150ms",
                "springBootAlone", "5-50ms",
                "withLibs", "20-150ms",
                "serverless", "EXCELLENT"
        ));
        comparison.put("startupTime", startup);

        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("jvm", Map.of(
                "heapMin", "128MB",
                "rssTypical", "200-500MB",
                "containerImage", "200-500MB"
        ));
        memory.put("native", Map.of(
                "heapMin", "8MB",
                "rssTypical", "30-80MB",
                "containerImage", "50-100MB"
        ));
        comparison.put("memoryUsage", memory);

        Map<String, Object> throughput = new LinkedHashMap<>();
        throughput.put("jvm", Map.of(
                "coldStart", "Slow (JIT warmup needed)",
                "100Req", "~95% of peak",
                "steadyState", "100% (JIT optimized)"
        ));
        throughput.put("native", Map.of(
                "coldStart", "Fast (no warmup)",
                "100Req", "~95% of peak",
                "steadyState", "~95% of JVM peak"
        ));
        comparison.put("throughput", throughput);

        Map<String, Object> deployment = new LinkedHashMap<>();
        deployment.put("jvm", Map.of(
                "binary", "JAR (20-50MB)",
                "runtime", "Required (JVM)",
                "containerBase", "eclipse-temurin:21",
                "scaling", "Slow cold start"
        ));
        deployment.put("native", Map.of(
                "binary", "Executable (60-120MB)",
                "runtime", "None needed",
                "containerBase", "scratch / distroless",
                "scaling", "Instant cold start"
        ));
        comparison.put("deployment", deployment);

        return comparison;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("mode", GraalVmAdvancedApplication.isNativeImage() ? "native" : "jvm");
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }
}
