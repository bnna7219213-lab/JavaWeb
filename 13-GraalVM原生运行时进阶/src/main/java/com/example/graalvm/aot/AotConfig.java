package com.example.graalvm.aot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * AOT Configuration - Advanced Edition
 *
 * This is the central configuration that aggregates ALL RuntimeHints registars.
 * Spring Boot AOT processor discovers these at build time and:
 *
 * 1. Serializes hints to JSON (META-INF/native-image/...)
 * 2. Generates optimized AOT classes
 * 3. native-image compiler uses these hints for closed-world analysis
 *
 * Registration Flow:
 *   mvn process-aot → discovers @ImportRuntimeHints → instantiates registrars
 *   → calls registerHints() → serializes to JSON → used by native-image
 *
 * Categories registered:
 *   - ReflectionHints:    Class.forName(), getDeclaredMethods(), field access
 *   - ResourceHints:      ClassLoader.getResource(), file I/O
 *   - SerializationHints: Java native serialization, Jackson JSON
 *   - ProxyHints:         Dynamic proxies (JDK Proxy, CGLIB)
 *   - JDKHints:           JDK internal types needing access
 */
@Configuration
@ImportRuntimeHints({
        ReflectionHints.class,
        ResourceHints.class,
        SerializationHints.class,
        ProxyHints.class,
        JdkHints.class
})
public class AotConfig {
    // Central import point for all RuntimeHints registars in the application.
    // Each hints class is responsible for one specific category.
}
