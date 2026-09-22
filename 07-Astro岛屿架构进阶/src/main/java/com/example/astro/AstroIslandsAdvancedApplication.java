package com.example.astro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Astro 岛屿架构进阶版 启动类
 *
 * <p>进阶特性：
 * <ul>
 *   <li>Alpine.js 做简单交互岛屿（搜索、折叠、Tab切换）</li>
 *   <li>React via CDN 做复杂交互岛屿</li>
 *   <li>多实体管理（User + Product）</li>
 *   <li>服务端模板渲染 + 岛屿按需水化</li>
 * </ul>
 */
@SpringBootApplication
public class AstroIslandsAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstroIslandsAdvancedApplication.class, args);
    }
}
