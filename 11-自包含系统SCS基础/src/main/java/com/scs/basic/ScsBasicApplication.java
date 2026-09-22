package com.scs.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SCS基础版 - 自包含用户服务
 *
 * <p>这是一个完整的Self-Contained System单元演示：</p>
 * <ul>
 *   <li>自有前端UI (Thymeleaf模板)</li>
 *   <li>自有后端API (REST接口)</li>
 *   <li>自有数据存储 (内存数据库模拟)</li>
 *   <li>自有消息系统 (Spring Events)</li>
 * </ul>
 *
 * <p>整个系统打包为一个独立jar，可独立部署运行。</p>
 */
@SpringBootApplication
@EnableAsync
public class ScsBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScsBasicApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  SCS 基础版 - 自包含用户服务");
        System.out.println("  访问地址: http://localhost:8099");
        System.out.println("  用户管理页面: http://localhost:8099/user/");
        System.out.println("  API基础路径: /api/v1/users");
        System.out.println("===========================================");
    }
}
