package com.aether.bff.controller;

import com.aether.bff.entity.DashboardVO;
import com.aether.bff.service.DashboardService;
import com.aether.bff.service.OrderService;
import com.aether.bff.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * BFF控制器（Backend For Frontend）
 *
 * <p>为前端定制的聚合API层。前端只需调用少数几个BFF接口，
 * 即可获得完整的页面数据，无需关心后端微服务的调用细节。</p>
 *
 * <p>路由前缀 /api/bff 表明这是BFF层的端点。</p>
 */
@RestController
@RequestMapping("/api/bff")
public class BffController {

    private static final Logger log = LoggerFactory.getLogger(BffController.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * Dashboard聚合接口 - BFF核心端点
     *
     * <p>一次调用聚合了：用户信息 + 近期订单 + 推荐商品 + 通知数。
     * 前端只需1次请求即可获得整个首页所需数据。</p>
     *
     * <p>GET /api/bff/dashboard</p>
     */
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        log.info("[BFF] 收到Dashboard聚合请求");

        long start = System.currentTimeMillis();
        DashboardVO dashboard = dashboardService.aggregateDashboard();
        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", dashboard);
        result.put("bffLayer", "basic");
        result.put("aggregationTimeMs", elapsed);

        log.info("[BFF] Dashboard响应完成，聚合耗时 {}ms", elapsed);
        return result;
    }

    /**
     * Dashboard聚合接口 - 并行优化版本
     *
     * <p>GET /api/bff/dashboard/parallel</p>
     */
    @GetMapping("/dashboard/parallel")
    public Map<String, Object> dashboardParallel() {
        log.info("[BFF] 收到并行Dashboard聚合请求");

        long start = System.currentTimeMillis();
        DashboardVO dashboard = dashboardService.aggregateDashboardParallel();
        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", dashboard);
        result.put("bffLayer", "basic-parallel");
        result.put("aggregationTimeMs", elapsed);

        log.info("[BFF] 并行Dashboard响应完成，聚合耗时 {}ms", elapsed);
        return result;
    }

    /**
     * 用户卡片接口 - 仅返回用户相关数据
     *
     * <p>演示BFF按需聚合：不是所有页面都需要完整Dashboard，
     * BFF可以为不同页面/组件提供不同的聚合粒度。</p>
     *
     * <p>GET /api/bff/user-card</p>
     */
    @GetMapping("/user-card")
    public Map<String, Object> userCard() {
        log.info("[BFF] 收到用户卡片请求");

        var user = userService.getCurrent();
        var orderCount = orderService.count();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", Map.of(
                "user", user,
                "orderCount", orderCount,
                "message", "欢迎回来！今日有3条新消息"
        ));

        return result;
    }

    /**
     * BFF服务状态检查
     *
     * <p>GET /api/bff/ping</p>
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "UP",
                "service", "BFF-Basic-Layer",
                "version", "1.0.0",
                "endpoints", new String[]{
                        "/api/bff/dashboard",
                        "/api/bff/dashboard/parallel",
                        "/api/bff/user-card"
                }
        );
    }
}
