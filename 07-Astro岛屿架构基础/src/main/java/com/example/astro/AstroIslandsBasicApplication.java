package com.example.astro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Astro岛屿架构基础版 启动类
 *
 * <p>核心理念：模拟 Astro 的“岛屿架构”：
 * <ul>
 *   <li>默认所有内容通过 Thymeleaf 服务端渲染为静态 HTML（首屏零 JS）</li>
 *   <li>仅在需要的区域加载 Alpine.js，实现“岛屿水化”</li>
 *   <li>达到极致的首屏性能和 SEO 友好性</li>
 * </ul>
 */
@SpringBootApplication
public class AstroIslandsBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstroIslandsBasicApplication.class, args);
    }
}
