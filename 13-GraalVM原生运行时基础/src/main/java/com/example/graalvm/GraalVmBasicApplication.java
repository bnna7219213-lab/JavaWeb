package com.example.graalvm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * GraalVM Native Image - 基础版启动类
 *
 * 此应用演示了Spring Boot 3.x的AOT(Ahead-of-Time)编译支持架构。
 * 通过GraalVM Native Image，此应用可被编译为原生机器码：
 *
 *  - 启动速度：毫秒级（vs JVM秒级）
 *  - 内存占用：极小（vs JVM百MB级别）
 *  - 无需JVM运行时部署
 *  - 适合Serverless和微服务场景
 *
 * 编译步骤:
 * 1. mvn spring-boot:build-image (使用Paketo Buildpacks)
 * 2. mvn -Pnative-compile package (使用native-maven-plugin)
 * 3. native-image -jar target/app.jar (直接使用GraalVM tool)
 */
@SpringBootApplication
public class GraalVmBasicApplication {

    private static long jvmStartTime;
    private static long appReadyTime;

    public static void main(String[] args) {
        jvmStartTime = System.currentTimeMillis();

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long vmStartTime = runtimeMXBean.getStartTime();
        System.out.println("================================================================");
        System.out.println("  GraalVM Native Image - Basic Demo");
        System.out.println("  JVM Start Timestamp: " + vmStartTime);
        System.out.println("  Spring Boot AOT Processing Enabled");
        System.out.println("================================================================");

        SpringApplication app = new SpringApplication(GraalVmBasicApplication.class);
        app.run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        appReadyTime = System.currentTimeMillis();
        long startupDuration = appReadyTime - jvmStartTime;

        System.out.println("");
        System.out.println("================================================================");
        System.out.println("  APPLICATION STARTED SUCCESSFULLY");
        System.out.println("================================================================");
        System.out.println("  Startup Time: " + startupDuration + " ms");
        System.out.println("  JVM Mode: " + isRunningOnGraalVM());
        System.out.println("  Server Port: 8103");
        System.out.println("  Health URL: http://localhost:8103/actuator/health");
        System.out.println("  Performance Panel: http://localhost:8103/");
        System.out.println("================================================================");
    }

    private String isRunningOnGraalVM() {
        try {
            String osArch = System.getProperty("org.graalvm.nativeimage.imagecode");
            if (osArch != null) {
                return "Native Image (GraalVM)";
            }
        } catch (Exception ignored) {}
        return "JVM HotSpot";
    }
}
