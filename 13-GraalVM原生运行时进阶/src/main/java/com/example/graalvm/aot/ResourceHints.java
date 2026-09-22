package com.example.graalvm.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Resource Hints Registrar
 *
 * ================================================================
 * Why is this needed?
 * ================================================================
 *
 * GraalVM Native Image builds a "closed world" — all resources that
 * may be loaded at runtime MUST be included in the binary at build time.
 *
 * Resource loading patterns that fail without hints:
 *   - getResourceAsStream("templates/index.html") → returns null
 *   - new File("config/application.yml") → may not find file
 *   - getClass().getResource("banner.txt") → returns null
 *
 * ================================================================
 * Resource registration strategies:
 * ================================================================
 * 1. registerPattern("glob")    — include matching files from classpath
 * 2. registerBundle("name")     — include ResourceBundle
 * 3. registerType(resourceType) — include specific resource class
 */
public class ResourceHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerTemplates(hints);
        registerConfigs(hints);
        registerStaticResources(hints);
        registerNativeImageConfigs(hints);
    }

    /**
     * Thymeleaf and other template files
     */
    private void registerTemplates(RuntimeHints hints) {
        // All template files
        hints.resources().registerPattern("templates/**");

        // Specific templates
        hints.resources().registerPattern("templates/index.html");
        hints.resources().registerPattern("templates/compare.html");
        hints.resources().registerPattern("templates/error.html");
    }

    /**
     * Application configuration files
     */
    private void registerConfigs(RuntimeHints hints) {
        // Properties files
        hints.resources().registerPattern("*.properties");
        hints.resources().registerPattern("*.yml");
        hints.resources().registerPattern("*.yaml");

        // Banner
        hints.resources().registerPattern("banner.txt");
    }

    /**
     * Static resources (CSS, JS, Images)
     */
    private void registerStaticResources(RuntimeHints hints) {
        // CSS
        hints.resources().registerPattern("static/css/**");

        // JavaScript
        hints.resources().registerPattern("static/js/**");

        // Images
        hints.resources().registerPattern("static/img/**");
        hints.resources().registerPattern("static/images/**");
        hints.resources().registerPattern("static/fonts/**");

        // Favicon
        hints.resources().registerPattern("static/favicon.ico");
        hints.resources().registerPattern("static/favicon.png");

        // General static content
        hints.resources().registerPattern("static/**");
        hints.resources().registerPattern("META-INF/resources/**");
    }

    /**
     * Native Image configuration files
     * These are typically in META-INF/native-image/
     */
    private void registerNativeImageConfigs(RuntimeHints hints) {
        hints.resources().registerPattern("META-INF/native-image/**");
        hints.resources().registerPattern("META-INF/services/**");
    }
}
