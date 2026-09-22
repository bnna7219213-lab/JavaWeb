package com.example.graalvm.aot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * AOT配置类
 *
 * 使用@ImportRuntimeHints注解将RuntimeHintsRegistrar注册到AOT处理流程中。
 *
 * 当执行 'mvn spring-boot:process-aot' 或 'mvn spring-boot:build-image' 时，
 * Spring Boot AOT处理器会：
 * 1. 实例化此配置类
 * 2. 发现和调用RuntimeHintsRegistrar
 * 3. 将hints序列化为JSON资源文件
 * 4. AOT引擎使用这些hints生成优化的启动代码
 * 5. Native Image编译器将注册信息包含在最终二进制中
 */
@Configuration
@ImportRuntimeHints(BasicRuntimeHintsRegistrar.class)
public class AotConfig {
    // 此配置类的唯一目的是导入RuntimeHints注册器
    // 所有实际的hints注册逻辑在 BasicRuntimeHintsRegistrar 中
}
