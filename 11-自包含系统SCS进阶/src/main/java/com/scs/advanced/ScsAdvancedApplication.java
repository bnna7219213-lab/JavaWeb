package com.scs.advanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SCS进阶版 - 多SCS协同
 *
 * <p>本项目演示多个Self-Contained System单元在一个应用中的协同工作。</p>
 *
 * <p>包含两个独立SCS单元：</p>
 * <ul>
 *   <li><strong>User SCS</strong> - 用户服务 (/user-scs/) - 管理用户注册/查询</li>
 *   <li><strong>Order SCS</strong> - 订单服务 (/order-scs/) - 管理订单/关联用户</li>
 * </ul>
 *
 * <p>核心演示：</p>
 * <ul>
 *   <li>用户SCS发布 UserCreated 事件</li>
 *   <li>Order SCS监听事件，自动创建默认订单数据</li>
 *   <li>每个SCS拥有自己的前端UI和API地址空间</li>
 *   <li>每个SCS拥有独立的数据存储（不同前缀的Map模拟）</li>
 * </ul>
 *
 * <p><strong>注意：</strong>为了演示方便，两个SCS在同一个Spring Boot应用中运行。
 * 在实际部署中，User SCS 和 Order SCS 应是两个独立的jar包，各自运行在不同的端口/服务器上。</p>
 */
@SpringBootApplication
@EnableAsync
public class ScsAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScsAdvancedApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  SCS 进阶版 - 多SCS协同");
        System.out.println("  访问地址: http://localhost:8100");
        System.out.println("  User SCS 页面: http://localhost:8100/user-scs/");
        System.out.println("  Order SCS 页面: http://localhost:8100/order-scs/");
        System.out.println("  SCS 架构说明: http://localhost:8100");
        System.out.println("===========================================");
    }
}
