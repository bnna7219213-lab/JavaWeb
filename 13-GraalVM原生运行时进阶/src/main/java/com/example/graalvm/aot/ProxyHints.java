package com.example.graalvm.aot;

import org.springframework.aot.hint.ProxyHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Proxy Hints Registrar
 *
 * ================================================================
 * Why is this needed?
 * ================================================================
 *
 * Dynamic proxies are used extensively in Spring Framework and Java ecosystem:
 *
 * 1. JDK Dynamic Proxies (java.lang.reflect.Proxy):
 *    - Spring AOP proxies (for @Transactional, @Cacheable, @Async)
 *    - Spring's @Repository proxies
 *    - RMI stubs
 *    - MyBatis Mapper interfaces
 *
 * 2. CGLIB Proxies:
 *    - Spring AOP for classes (not implementing interfaces)
 *    - @Configuration class proxies
 *    - @Lazy initialization proxies
 *
 * Without proxy hints:
 *   - @Transactional methods won't work
 *   - Spring AOP aspects won't be applied
 *   - Repository interfaces will fail at initialization
 *
 * ================================================================
 * How it works:
 * ================================================================
 *
 * native-image needs to know at BUILD TIME what proxies will be created
 * at runtime. This allows it to pre-generate proxy classes and avoid
 * runtime bytecode generation.
 *
 * The registration tells the compiler:
 * "These interfaces (and their super-interfaces) may be used with
 * java.lang.reflect.Proxy.newProxyInstance() at runtime."
 */
@org.springframework.aot.hint.annotation.Reflective
public class ProxyHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerServiceProxies(hints);
        registerSpringAopProxies(hints);
        registerRepositoryProxies(hints);
    }

    /**
     * Register interfaces that may be proxied by Spring's JDK Dynamic Proxy.
     *
     * Spring uses JDK proxies for interfaces. Registering these ensures
     * the generated proxy classes are available at runtime.
     */
    private void registerServiceProxies(RuntimeHints hints) {
        // List interfaces that use JDK dynamic proxies.
        // Example: if UserService were an interface with implementations:
        // hints.proxies().registerJdkProxy(
        //     new TypeReference<com.example.UserService>() {},
        //     new TypeReference<org.springframework.context.ApplicationContextAware>() {}
        // );

        // Spring Data JPA repository example:
        // hints.proxies().registerJdkProxy(
        //     new TypeReference<org.springframework.data.repository.CrudRepository<User, Long>>() {}
        // );
    }

    /**
     * Register Spring AOP-related proxies.
     *
     * Spring AOP creates proxies for beans with advice applied.
     * The pointcut-matched beans get proxied transparently.
     */
    private void registerSpringAopProxies(RuntimeHints hints) {
        // Spring automatically register proxies for @Transactional, @Async, @Cacheable
        // but you can add additional ones if using programmatic AOP
    }

    /**
     * Register repository proxies.
     *
     * If using Spring Data JPA, repository interfaces need proxy registration
     * since they're proxied at runtime by Spring Data.
     */
    private void registerRepositoryProxies(RuntimeHints hints) {
        // Example for Spring Data:
        /* hints.proxies().registerJdkProxy(
            new TypeReference<org.springframework.data.repository.Repository<User, Long>>() {},
            new TypeReference<java.lang.AutoCloseable>() {}
        ); */
    }
}
