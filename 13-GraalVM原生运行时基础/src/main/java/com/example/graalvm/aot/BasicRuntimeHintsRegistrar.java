package com.example.graalvm.aot;

import com.example.graalvm.entity.Order;
import com.example.graalvm.entity.User;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/**
 * GraalVM Native Image - 运行时Hints注册器（基础版）
 *
 * ================================================================
 * 为什么需要RuntimeHintsRegistrar？
 * ================================================================
 *
 * GraalVM Native Image 使用"closed world"假设：
 *   所有在运行时可能被调用的类、方法、字段必须在编译时确定。
 *
 * 然而，Java生态大量使用"Open World"特性：
 *   - 反射 (Reflection): Class.forName(), getDeclaredMethods()
 *   - 动态代理 (Dynamic Proxy): Proxy.newProxyInstance()
 *   - 资源加载 (Resource Loading): ClassLoader.getResource()
 *   - 序列化 (Serialization): Java Serialization, Jackson JSON
 *   - JNI调用 (Java Native Interface)
 *
 * Native Image编译器无法自动检测这些动态行为，
 * 必须通过RuntimeHints手动注册需要保留的类/资源/代理等。
 *
 * ================================================================
 * Spring Boot AOT处理过程：
 * ================================================================
 *   1. 构建时执行process-aot阶段
 *   2. AOT引擎分析应用上下文，生成优化后的代码
 *   3. 收集RuntimeHints并序列化
 *   4. 编译时(native-image)读取hints配置
 *   5. 将注册的反射/资源/代理等包含在最终二进制中
 *
 * ================================================================
 * 基础版注册内容:
 * ================================================================
 *   - User/Order实体反射注册 (用于Jackson JSON序列化)
 *   - Thymeleaf模板资源注册
 *   - 基本JSR-310日期时间类型注册
 */
public class BasicRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerReflectionHints(hints);
        registerResourceHints(hints);
        registerSerializationHints(hints);
    }

    /**
     * 注册反射Hints - 用于Jackson JSON序列化和Spring bean注入
     *
     * MemberCategory.values() 注册所有可能的反射操作:
     * - PUBLIC_FIELDS, DECLARED_FIELDS (字段访问)
     * - PUBLIC_METHODS, DECLARED_METHODS (方法调用)
     * - PUBLIC_CONSTRUCTORS, DECLARED_CONSTRUCTORS (构造器)
     */
    private void registerReflectionHints(RuntimeHints hints) {
        // 注册实体类 - 允许完整的反射操作（用于JSON反序列化）
        hints.reflection().registerType(User.class, MemberCategory.values());
        hints.reflection().registerType(Order.class, MemberCategory.values());

        // 注册常用JDK类型（如果在实体字段中使用）
        hints.reflection().registerType(java.math.BigDecimal.class, MemberCategory.values());
        hints.reflection().registerType(java.time.LocalDateTime.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.PUBLIC_FIELDS);
        hints.reflection().registerType(java.time.LocalDate.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(java.time.LocalTime.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS);
    }

    /**
     * 注册资源Hints - 确保模板文件和配置文件可被加载
     */
    private void registerResourceHints(RuntimeHints hints) {
        // Thymeleaf模板文件
        hints.resources().registerPattern("templates/**");

        // 配置文件
        hints.resources().registerPattern("*.properties");
        hints.resources().registerPattern("*.yml");
        hints.resources().registerPattern("*.yaml");

        // Banner
        hints.resources().registerPattern("banner.txt");
    }

    /**
     * 注册序列化Hints - 确保Java序列化正常工作
     */
    private void registerSerializationHints(RuntimeHints hints) {
        // 注册实体类用于Java原生序列化
        hints.serialization().registerType(User.class);
        hints.serialization().registerType(Order.class);
    }
}
