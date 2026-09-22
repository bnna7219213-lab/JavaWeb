package com.example.modulith.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Modulith 模块化单体应用 - 基础版
 *
 * 模块结构：
 * - user模块：用户管理
 * - product模块：商品管理
 * - order模块：订单管理（依赖user和product模块的API）
 *
 * 设计原则：
 * 1. 每个模块通过api包暴露接口，禁止其他模块直接引用内部实现
 * 2. 模块间通过接口（Service接口）通信
 * 3. 模块间通信由Spring ApplicationEvent驱动（可选）
 */
@SpringBootApplication
public class ModulithBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithBasicApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Spring Modulith 模块化单体基础版已启动");
        System.out.println("  访问地址: http://localhost:8101");
        System.out.println("===========================================");
    }
}
