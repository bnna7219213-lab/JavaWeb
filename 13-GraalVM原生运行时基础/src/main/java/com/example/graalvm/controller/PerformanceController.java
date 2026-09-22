package com.example.graalvm.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 性能监控控制器
 *
 * 提供启动时间、内存占用等关键性能指标API。
 */
@RestController
@RequestMapping("/api/perf")
public class PerformanceController {

    private static final long START_TIME = System.currentTimeMillis();
    private long appReadyTime;

    @Value("${spring.application.name:unknown}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        this.appReadyTime = System.currentTimeMillis();
    }

    /**
     * 获取当前性能指标
     */
    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        Map<String, Object> startup = new HashMap<>();
        long ready = appReadyTime > 0 ? appReadyTime : System.currentTimeMillis();
        startup.put("startupTimeMs", ready - START_TIME);
        startup.put("jvmStartTimeMs", runtimeMXBean.getStartTime());
        startup.put("uptimeMs", runtimeMXBean.getUptime());
        metrics.put("startup", startup);

        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsedMB", memoryMXBean.getHeapMemoryUsage().getUsed() / 1024.0 / 1024.0);
        memory.put("heapMaxMB", memoryMXBean.getHeapMemoryUsage().getMax() / 1024.0 / 1024.0);
        memory.put("heapCommittedMB", memoryMXBean.getHeapMemoryUsage().getCommitted() / 1024.0 / 1024.0);
        memory.put("nonHeapUsedMB", memoryMXBean.getNonHeapMemoryUsage().getUsed() / 1024.0 / 1024.0);
        memory.put("freeMemoryMB", runtime.freeMemory() / 1024.0 / 1024.0);
        memory.put("totalMemoryMB", runtime.totalMemory() / 1024.0 / 1024.0);
        memory.put("maxMemoryMB", runtime.maxMemory() / 1024.0 / 1024.0);
        metrics.put("memory", memory);

        Map<String, Object> runtimeInfo = new HashMap<>();
        runtimeInfo.put("appName", appName);
        runtimeInfo.put("javaVersion", System.getProperty("java.version"));
        runtimeInfo.put("javaVendor", System.getProperty("java.vendor"));
        runtimeInfo.put("osName", System.getProperty("os.name"));
        runtimeInfo.put("osArch", System.getProperty("os.arch"));
        runtimeInfo.put("availableProcessors", runtime.availableProcessors());
        runtimeInfo.put("isNativeImage", isNativeImage());
        runtimeInfo.put("timestamp", Instant.now().toString());
        metrics.put("runtime", runtimeInfo);

        return metrics;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("mode", isNativeImage() ? "native" : "jvm");
        return ResponseEntity.ok(status);
    }

    private boolean isNativeImage() {
        try {
            return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
        } catch (Exception e) {
            return false;
        }
    }
}
