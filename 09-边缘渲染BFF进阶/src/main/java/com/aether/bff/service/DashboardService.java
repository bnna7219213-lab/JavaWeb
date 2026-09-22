package com.aether.bff.service;

import com.aether.bff.entity.DashboardVO;
import com.aether.bff.entity.Order;
import com.aether.bff.entity.Product;
import com.aether.bff.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Dashboard聚合服务（BFF核心聚合层）- 进阶版
 *
 * <p>特性：</p>
 * <ul>
 *   <li>{@code @Cacheable} 模拟边缘CDN缓存：首次聚合后缓存结果</li>
 *   <li>CompletableFuture 并行调用最优内部服务</li>
 *   <li>多端适配：PC和Mobile调用不同的推荐服务方法</li>
 *   <li>{@code @CacheEvict} 提供缓存失效入口</li>
 * </ul>
 *
 * <p>缓存策略说明：</p>
 * <pre>
 *  用户请求 → BFF层 @Cacheable检查缓存
 *     ├── 命中缓存 → 直接返回（0ms聚合时间）
 *     └── 未命中 → 并行调用内部服务 → 聚合 → 写入缓存 → 返回
 * </pre>
 */
@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RecommendService recommendService;

    /**
     * PC端Dashboard聚合（带缓存）
     *
     * <p>{@code @Cacheable(value = "dashboard", key = "'pc-dashboard'")} 含义：</p>
     * <ul>
     *   <li>value = "dashboard" → 缓存空间名</li>
     *   <li>key = "'pc-dashboard'" → 缓存键（固定值，PC端共享缓存）</li>
     *   <li>首次调用执行方法体并缓存结果，后续调用直接返回缓存值</li>
     * </ul>
     *
     * <p>优化效果：</p>
     * <pre>
     *   首次请求: 并行调用3个服务 ~200ms + 组装 = ~210ms
     *   后续请求: 缓存命中 ~1ms（100倍性能提升）
     * </pre>
     */
    @Cacheable(value = "dashboard", key = "'pc-dashboard'")
    public DashboardVO getPcDashboard() {
        log.info("[DashboardService] PC端Dashboard缓存未命中 - 开始并行聚合...");
        long start = System.currentTimeMillis();

        // 并行调用用户和订单服务
        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(userService::getCurrent);
        CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(orderService::recent);

        // 获取用户后并行获取PC端推荐
        User user = userFuture.join();
        CompletableFuture<List<Product>> recsFuture = CompletableFuture.supplyAsync(recommendService::forPc);

        List<Order> orders = ordersFuture.join();
        List<Product> recs = recsFuture.join();

        DashboardVO dashboard = new DashboardVO(user, orders, recs, "pc");

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DashboardService] PC端Dashboard聚合完成，耗时 {}ms", elapsed);
        return dashboard;
    }

    /**
     * 移动端Dashboard聚合（带缓存）
     *
     * <p>使用独立的缓存键 'mobile-dashboard'，与PC端缓存隔离。
     * 移动端返回更精简的数据（3个推荐 + 快捷入口）。</p>
     */
    @Cacheable(value = "dashboard", key = "'mobile-dashboard'")
    public DashboardVO getMobileDashboard() {
        log.info("[DashboardService] 移动端Dashboard缓存未命中 - 开始并行聚合...");
        long start = System.currentTimeMillis();

        // 移动端精简：并行获取用户、服务和精简推荐
        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(userService::getCurrent);
        CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(orderService::recent);
        CompletableFuture<List<Product>> recsFuture = CompletableFuture.supplyAsync(recommendService::forMobile);

        User user = userFuture.join();
        List<Order> orders = ordersFuture.join();
        List<Product> recs = recsFuture.join();

        DashboardVO dashboard = new DashboardVO(user, orders, recs, "mobile");

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DashboardService] 移动端Dashboard聚合完成（精简数据），耗时 {}ms", elapsed);
        return dashboard;
    }

    /**
     * 清除Dashboard缓存
     *
     * <p>当底层数据源发生变化时（如用户下单、商品推荐更新），
     * 清除缓存以确保下次请求获取最新数据。</p>
     *
     * <p>{@code allEntries = true} 清除 "dashboard" 缓存空间下的所有条目。</p>
     */
    @CacheEvict(value = "dashboard", allEntries = true)
    public void evictDashboardCache() {
        log.info("[DashboardService] 清除所有Dashboard缓存");
    }
}
