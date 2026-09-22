package com.aether.bff.service;

import com.aether.bff.entity.Order;
import com.aether.bff.entity.Order.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 订单服务（模拟内部微服务）- 进阶版
 *
 * <p>订单数据变化频率较低，适合较长时间缓存。</p>
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * 获取用户近期订单
     *
     * <p>{@code @Cacheable(value = "order-detail")} 缓存订单数据。
     * 实际应用中此缓存应由"订单创建/状态变更"事件触发失效。</p>
     */
    @Cacheable(value = "order-detail", key = "'recent-orders'")
    public List<Order> recent() {
        log.info("[OrderService] 缓存未命中 - 实际调用订单微服务 getRecentOrders()");
        mockNetworkDelay(120);

        Order order1 = new Order(
                "ORD-20240115-001", "SHIPPED", 368.00,
                LocalDateTime.of(2024, 1, 15, 14, 30),
                Arrays.asList(
                        new OrderItem("P-101", "无线降噪耳机", "🎧", 1, 299.00),
                        new OrderItem("P-102", "Type-C充电线", "🔌", 2, 34.50)
                )
        );

        Order order2 = new Order(
                "ORD-20240110-002", "DELIVERED", 1299.00,
                LocalDateTime.of(2024, 1, 10, 9, 15),
                Arrays.asList(
                        new OrderItem("P-201", "机械键盘", "⌨️", 1, 599.00),
                        new OrderItem("P-202", "鼠标垫", "🖱️", 1, 89.00),
                        new OrderItem("P-203", "显示器支架", "🖥️", 1, 129.00)
                )
        );

        Order order3 = new Order(
                "ORD-20240105-003", "PENDING", 45.00,
                LocalDateTime.of(2024, 1, 5, 20, 0),
                Arrays.asList(
                        new OrderItem("P-301", "笔记本贴纸", "📝", 3, 15.00)
                )
        );

        return Arrays.asList(order1, order2, order3);
    }

    public Integer count() {
        mockNetworkDelay(50);
        return 28;
    }

    private void mockNetworkDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
