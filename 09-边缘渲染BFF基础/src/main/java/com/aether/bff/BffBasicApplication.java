package com.aether.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 边缘渲染BFF基础版 - 启动类
 *
 * <p>架构说明：模拟"边缘层BFF"（Backend For Frontend）概念。</p>
 * <ul>
 *   <li>BFF层 = 为特定前端定制的API聚合层</li>
 *   <li>前端 = 独立静态SPA页面，只需调用一个聚合接口即可获得完整页面数据</li>
 *   <li>核心价值：服务端聚合多个内部服务数据，减少前端请求数</li>
 * </ul>
 *
 * <p>分层架构：</p>
 * <pre>
 *   前端SPA (8095/index.html)
 *        | 1次HTTP请求
 *        v
 *   BFF层 (BffController)
 *        | 内部并行调用
 *        v
 *   [用户服务] [订单服务] [推荐服务]  (模拟微服务)
 * </pre>
 */
@SpringBootApplication
public class BffBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffBasicApplication.class, args);
        System.out.println("============================================");
        System.out.println("  边缘渲染BFF基础版 已启动");
        System.out.println("  API入口: http://localhost:8095/api/bff/dashboard");
        System.out.println("  前端页面: http://localhost:8095/index.html");
        System.out.println("============================================");
    }
}
