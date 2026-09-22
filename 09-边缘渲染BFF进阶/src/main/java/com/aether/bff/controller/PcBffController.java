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
 * PC端BFF控制器
 *
 * <p>为PC Web前端定制的聚合API端点。</p>
 *
 * <p>与移动端的差异：</p>
 * <ul>
 *   <li>返回更丰富的数据（6个推荐商品 vs 3个）</li>
 *   <li>包含PC特有的字段（详细统计、侧边栏数据）</li>
 *   <li>使用独立的缓存键，与移动端缓存隔离</li> </ul>
 *
 * <p>路由前缀: /api/pc</p>
 */
@RestController
@RequestMapping("/api/pc")
public class PcBffController {

    private static final Logger log = LoggerFactory.getLogger(PcBffController.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * PC端Dashboard聚合接口
     *
     * <p>GET /api/pc/dashboard</p>
     *
     * <p>前端只需调用此一个接口即可获得PC首页所需的全部数据。</p>
     */
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        log.info("[PC-BFF] 收到PC端Dashboard请求");

        long start = System.currentTimeMillis();
        DashboardVO dashboard = dashboardService.getPcDashboard();
        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", dashboard);
        result.put("deviceType", "pc");
        result.put("aggregationTimeMs", elapsed);
        result.put("cached", elapsed < 10);

        log.info("[PC-BFF] 响应完成，耗时 {}ms（缓存{}命中）", elapsed, elapsed < 10 ? "已" : "未");
        return result;
    }

    /**
     * PC端用户卡片
     *
     * <p>GET /api/pc/user-card</p>
     */
    @GetMapping("/user-card")
    public Map<String, Object> userCard() {
        log.info("[PC-BFF] 收到用户卡片请求");
        var user = userService.getCurrent();
        var orderCount = orderService.count();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", Map.of(
                "user", user,
                "orderCount", orderCount,
                "memberLevel", user.getMemberLevel(),
                "points", user.getPoints(),
                "suggestion", "PC端可享受大屏专属优惠"
        ));
        return result;
    }

    /**
     * PC端服务状态
     *
     * <p>GET /api/pc/ping</p>
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "UP",
                "device", "PC-Web",
                "service", "BFF-Advanced-PC",
                "version", "1.0.0",
                "endpoints", new String[]{
                        "/api/pc/dashboard",
                        "/api/pc/user-card"
                }
        );
    }
}
