package com.example.modulith.advanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Modulith 模块化单体应用 - 进阶版
 *
 * 模块结构（5个模块）：
 * - user模块：用户管理
 * - product模块：商品管理
 * - order模块：订单管理（依赖user和product模块的API）
 * - notification模块：通知服务（事件驱动，监听订单/用户事件）
 * - audit模块：审计服务（事件驱动，监听所有模块事件）
 *
 * 进阶特性：
 * 1. 更多模块（notification, audit）
 * 2. 事件驱动解耦（@EventListener跨模块监听）
 * 3. ApplicationModule概念（api包 + internal包）
 * 4. ModularityTest - 完整验证规则
 * 5. 模块间DIP（依赖倒置）完整实现
 */
@SpringBootApplication
public class ModulithAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithAdvancedApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Spring Modulith 模块化单体进阶版已启动");
        System.out.println("  访问地址: http://localhost:8102");
        System.out.println("  模块: user, product, order, notification, audit");
        System.out.println("===========================================");
    }
}
