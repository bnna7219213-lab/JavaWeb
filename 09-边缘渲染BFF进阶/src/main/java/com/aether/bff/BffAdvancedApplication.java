package com.aether.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 边缘渲染BFF进阶版 - 启动类
 *
 * <p>完整BFF+边缘渲染分层架构，包含以下核心特性：</p>
 * <ul>
 *   <li>BFF聚合API：服务端并行聚合多个内部服务数据</li>
 *   <li>服务端渲染（SSR）：BFF层直接输出HTML片段，减少前端渲染负担</li>
 *   <li>模拟边缘CDN缓存：Spring Cache实现接口响应缓存 + 失效策略</li>
 *   <li>多端适配：PcBffController / MobileBffController 区分PC和移动端API</li>
 * </ul>
 */
@SpringBootApplication
@EnableCaching
public class BffAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffAdvancedApplication.class, args);
        System.out.println("================================================");
        System.out.println("  边缘渲染BFF进阶版 已启动 (端口8096)");
        System.out.println("  PC端BFF:  http://localhost:8096/api/pc/dashboard");
        System.out.println("  移动端BFF: http://localhost:8096/api/mobile/dashboard");
        System.out.println("  SSR渲染:  http://localhost:8096/api/ssr/render/home");
        System.out.println("  前端页面: http://localhost:8096/index.html");
        System.out.println("================================================");
    }
}
