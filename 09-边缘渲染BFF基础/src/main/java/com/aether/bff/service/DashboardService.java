package com.aether.bff.service;

import com.aether.bff.entity.DashboardVO;
import com.aether.bff.entity.Order;
import com.aether.bff.entity.Product;
import com.aether.bff.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Dashboard聚合服务（BFF核心聚合层）
 *
 * <p>这是BFF模式的核心——将多个内部服务的数据聚合为前端所需的Dashboard视图。</p>
 *
 * <p>工作流程：</p>
 * <ol>
 *   <li>调用 UserService 获取用户信息</li>
 *   <li>调用 OrderService 获取近期订单</li>
 *   <li>调用 RecommendService 获取个性化推荐</li>
 *   <li>组装为 DashboardVO 返回给控制器</li>
 * </ol>
 *
 * <p>优化方向（进阶版）：</p>
 * <ul>
 *   <li>使用 CompletableFuture 并行调用内部服务，减少总耗时</li>
 *   <li>添加缓存层，避免重复计算</li>
 *   <li>针对不同端返回不同粒度的数据</li>
 * </ul>
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
     * 聚合Dashboard数据（串行调用版本）
     *
     * <p>依次调用三个内部服务，聚合结果后返回。
     * 总耗时 ≈ max(用户服务, 订单服务, 推荐服务) 的顺序执行和。</p>
     *
     * @return 聚合后的Dashboard视图
     */
    public DashboardVO aggregateDashboard() {
        log.info("[DashboardService] 开始聚合Dashboard数据...");

        long start = System.currentTimeMillis();

        // Step 1: 获取用户信息
        User user = userService.getCurrent();

        // Step 2: 获取近期订单
        List<Order> orders = orderService.recent();

        // Step 3: 获取个性化推荐
        List<Product> recs = recommendService.forUser(user.getId());

        // Step 4: 组装Dashboard
        DashboardVO dashboard = new DashboardVO(user, orders, recs);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DashboardService] Dashboard聚合完成，耗时 {}ms", elapsed);

        return dashboard;
    }

    /**
     * 并行聚合Dashboard数据（优化版）
     *
     * <p>使用 CompletableFuture 并行调用三个内部服务，
     * 总耗时 ≈ 最慢的那个服务响应时间。</p>
     *
     * @return 聚合后的Dashboard视图
     */
    public DashboardVO aggregateDashboardParallel() {
        log.info("[DashboardService] 开始并行聚合Dashboard数据...");

        long start = System.currentTimeMillis();

        // 并行发起三个服务调用
        var userFuture = java.util.concurrent.CompletableFuture.supplyAsync(userService::getCurrent);
        var ordersFuture = java.util.concurrent.CompletableFuture.supplyAsync(orderService::recent);

        // 推荐服务依赖用户ID，串行执行
        User user = userFuture.join();
        var recsFuture = java.util.concurrent.CompletableFuture.supplyAsync(() -> recommendService.forUser(user.getId()));

        List<Order> orders = ordersFuture.join();
        List<Product> recs = recsFuture.join();

        DashboardVO dashboard = new DashboardVO(user, orders, recs);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DashboardService] 并行聚合完成，耗时 {}ms（串行预估 ~400ms）", elapsed);

        return dashboard;
    }
}
