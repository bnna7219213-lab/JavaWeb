package com.example.hilla.service;

import com.example.hilla.entity.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 订单服务层（内存实现，模拟数据库操作）
 */
@Service
public class OrderService {

    private final Map<Long, Order> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public OrderService() {
        // 初始化测试数据
        save(new Order(null, 1, "MacBook Pro 14", 1, new BigDecimal("14999.00"), "PAID"));
        save(new Order(null, 1, "AirPods Pro", 2, new BigDecimal("1899.00"), "SHIPPED"));
        save(new Order(null, 2, "机械键盘 Keychron K2", 1, new BigDecimal("598.00"), "PENDING"));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orderStore.values());
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderStore.get(id));
    }

    public List<Order> findByUserId(Integer userId) {
        return orderStore.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.incrementAndGet());
        }
        orderStore.put(order.getId(), order);
        return order;
    }

    public Order update(Long id, Order order) {
        if (!orderStore.containsKey(id)) {
            return null;
        }
        order.setId(id);
        orderStore.put(id, order);
        return order;
    }

    public boolean deleteById(Long id) {
        return orderStore.remove(id) != null;
    }
}
