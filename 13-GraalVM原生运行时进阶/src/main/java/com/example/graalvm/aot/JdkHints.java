package com.example.graalvm.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class JdkHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(java.lang.management.ManagementFactory.class);
        hints.reflection().registerType(java.lang.management.RuntimeMXBean.class);
        hints.reflection().registerType(java.lang.management.MemoryMXBean.class);
        hints.reflection().registerType(java.lang.management.ThreadMXBean.class);
        hints.reflection().registerType(java.lang.management.GarbageCollectorMXBean.class);
        hints.reflection().registerType(java.lang.management.MemoryPoolMXBean.class);
        hints.reflection().registerType(java.lang.management.MemoryUsage.class);
        hints.reflection().registerType(java.lang.management.MemoryType.class);
        hints.reflection().registerType(java.security.SecureRandom.class);
        hints.reflection().registerType(java.util.concurrent.atomic.AtomicLong.class);
        hints.reflection().registerType(java.util.concurrent.atomic.AtomicInteger.class);
        hints.reflection().registerType(java.util.concurrent.ConcurrentHashMap.class);
        hints.reflection().registerType(java.util.logging.Logger.class);
        hints.reflection().registerType(java.util.logging.Level.class);
        hints.reflection().registerType(java.nio.charset.Charset.class);
        hints.reflection().registerType(java.nio.charset.StandardCharsets.class);
    }
}
