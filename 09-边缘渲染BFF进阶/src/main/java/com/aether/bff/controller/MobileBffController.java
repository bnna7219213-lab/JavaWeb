package com.aether.bff.controller;

import com.aether.bff.entity.DashboardVO;
import com.aether.bff.service.DashboardService;
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
 * 移动端BFF控制器（H5/App前端）
 *
 * <p>为移动端(H5/小程序/App)定制的聚合API端点。</p>
 *
 * <p>与PC端的核心差异：</p>
 * <ul>
 *   <li>更精简的数据量（3个推荐 vs 6个）减少移动网络传输</li>
 *   <li>包含移动端特有字段：quickActions（快捷入口）</li>
 *   <li>使用独立的缓存键 'mobile-dashboard'</li>
 *   <li>字段命名偏向移动端UI组件需要</li>
 * </ul>
 *
 * <p>路由前缀: /api/mobile</p>
 */
@RestController
@RequestMapping("/api/mobile")
public class MobileBffController {

    private static final Logger log = LoggerFactory.getLogger(MobileBffController.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    /**
     * 移动端Dashboard
     *
     * <p>GET /api/mobile/dashboard</p>
     *
     * <p>移动端特点：数据精简、响应更快、包含快捷入口。</p>
     */
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        log.info("[Mobile-BFF] 收到移动端Dashboard请求");

        long start = System.currentTimeMillis();
        DashboardVO dashboard = dashboardService.getMobileDashboard();
        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", dashboard);
        result.put("deviceType", "mobile");
        result.put("aggregationTimeMs", elapsed);
        result.put("cached", elapsed < 10);

        log.info("[Mobile-BFF] 响应完成，耗时 {}ms（精简数据版）", elapsed);
        return result;
    }

    /**
     * 移动端首页精简数据
     *
     * <p>GET /api/mobile/home-feed</p>
     *
     * <p>专为移动端信息流设计，仅返回核心展示数据。</p>
     */
    @GetMapping("/home-feed")
    public Map<String, Object> homeFeed() {
        log.info("[Mobile-BFF] 收到首页信息流请求");

        var user = userService.getCurrent();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", Map.of(
                "greeting", "Hi，" + user.getUsername(),
                "memberLevel", user.getMemberLevel(),
                "unreadCount", 3,
                "bannerList", new String[]{
                        "🎉 新用户专享优惠",
                        "🚚 限时免运费活动开启",
                }
        ));
        return result;
    }

    /**
     * 移动端服务状态
     *
     * <p>GET /api/mobile/ping</p>
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "UP",
                "device", "Mobile-H5",
                "service", "BFF-Advanced-Mobile",
                "version", "1.0.0",
                "endpoints", new String[]{
                        "/api/mobile/dashboard",
                        "/api/mobile/home-feed"
                }
        );
    }
}
