package com.example.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 原生 ESM 微前端基础 - Spring Boot 启动类
 * 使用内嵌 Tomcat 作为静态资源服务器
 * 端口: 8093
 */
@SpringBootApplication
public class EsmMicroFrontendBasicApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsmMicroFrontendBasicApplication.class, args);
    }
}
