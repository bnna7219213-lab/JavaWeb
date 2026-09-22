package com.example.graalvm;

import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * GraalVM Native Image - 进阶版启动类
 *
 * 完整生产级AOT/Native Image架构演示，包含：
 * 1. 完整的RuntimeHints注册（反射、资源、序列化、代理）
 * 2. JVM和Native两种模式的自动检测
 * 3. 深度性能监控和报告
 * 4. 启动时间精确测量
 *
 * @UnstableApi 表明部分GraalVM集成的API仍在演进中
 *
 * 编译路径:
 *   mvn spring-boot:build-image (Paketo方式，无需本地GraalVM)
 *   mvn -Pnative package (本地GraalVM方式)
 *   docker build -t graalvm-native-advanced . (Docker方式)
 */
@SpringBootApplication
public class GraalVmAdvancedApplication {

    private static final long APP_START_NANO = System.nanoTime();
    private static volatile long appReadyNanos;

    public static void main(String[] args) {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        System.out.println("================================================================");
        System.out.println("  GraalVM Native Image - Advanced Demo");
        System.out.println("  Spring Boot 3.x Full AOT Configuration");
        System.out.println("  JVM Start: " + Instant.ofEpochMilli(rb.getStartTime()));
        System.out.println("  Running on: " + detectRuntimeMode());
        System.out.println("================================================================");

        SpringApplication app = new SpringApplication(GraalVmAdvancedApplication.class);

        // Native Image specific tuning
        if (isNativeImage()) {
            // Native image runs with serial GC by default
            // Fewer optimizations needed at runtime
            System.out.println("  [Native] Optimized for native image execution");
        } else {
            // JVM mode: suggest G1GC for container environments
            System.out.println("  [JVM] Running on HotSpot JVM");
        }

        app.run(args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        long elapsedMs = Duration.ofNanos(System.nanoTime() - APP_START_NANO).toMillis();
        System.out.println("  Application started in " + elapsedMs + "ms");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        appReadyNanos = System.nanoTime();
        long startupMs = Duration.ofNanos(appReadyNanos - APP_START_NANO).toMillis();

        System.out.println("");
        System.out.println("================================================================");
        System.out.println("  APPLICATION READY");
        System.out.println("================================================================");
        System.out.println("  Total Startup Time:  " + startupMs + " ms");
        System.out.println("  Runtime Mode:        " + detectRuntimeMode());
        System.out.println("  Server Port:         8104");
        System.out.println("  Health Check:        http://localhost:8104/api/perf/health");
        System.out.println("  Metrics:             http://localhost:8104/api/perf/metrics");
        System.out.println("  Comparison Panel:    http://localhost:8104/compare");
        System.out.println("  Performance Panel:   http://localhost:8104/");
        System.out.println("================================================================");

        // Print memory summary
        printMemorySummary();
    }

    private static void printMemorySummary() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBean();
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        System.out.println("  Memory Summary:");
        System.out.printf("    Heap Used:  %.1f MB / %.1f MB%n",
                mem.getHeapMemoryUsage().getUsed() / 1024.0 / 1024.0,
                mem.getHeapMemoryUsage().getCommitted() / 1024.0 / 1024.0);
        System.out.printf("    Non-Heap:   %.1f MB%n",
                mem.getNonHeapMemoryUsage().getUsed() / 1024.0 / 1024.0);

        for (MemoryPoolMXBean pool : pools) {
            String name = pool.getName();
            long used = pool.getUsage().getUsed();
            long max = pool.getUsage().getMax();
            if (used > 0 && !name.contains("Cache")) {
                System.out.printf("    %-20s %.1f MB / %.1f MB%n", name + ":",
                        used / 1024.0 / 1024.0,
                        max > 0 ? max / 1024.0 / 1024.0 : 0);
            }
        }

        for (GarbageCollectorMXBean gc : gcBeans) {
            System.out.printf("    GC: %-20s count=%d time=%dms%n",
                    gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
        System.out.println("");
    }

    public static String detectRuntimeMode() {
        try {
            // Method 1: Check system property
            String imageCode = System.getProperty("org.graalvm.nativeimage.imagecode");
            if (imageCode != null) {
                return "Native Image (GraalVM) - imagecode=" + imagecode;
            }

            // Method 2: Check if running in native image via reflection
            boolean isNativeImage = System.getProperty("java.home", "").isEmpty()
                    || System.getProperty("org.graalvm.nativeimage.kind") != null;
            if (isNativeImage) {
                return "Native Image (GraalVM)";
            }
        } catch (Exception ignored) {
        }
        return "JVM HotSpot (" + System.getProperty("java.version") + ")";
    }

    public static boolean isNativeImage() {
        try {
            return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static long getStartupTimeMs() {
        if (appReadyNanos > 0) {
            return Duration.ofNanos(appReadyNanos - APP_START_NANO).toMillis();
        }
        return Duration.ofNanos(System.nanoTime() - APP_START_NANO).toMillis();
    }
}
