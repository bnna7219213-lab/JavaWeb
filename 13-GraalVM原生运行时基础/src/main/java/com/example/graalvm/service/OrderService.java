package com.example.graalvm.service;

import com.example.graalvm.entity.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单服务层
 */
@Service
public class OrderService {

    private final Map<Long, Order> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderService() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        createOrder(new Order(null, 1L, "MacBook Pro 16", new BigDecimal("24999.00"), 1, "COMPLETED"));
        createOrder(new Order(null, 1L, "iPhone 15 Pro", new BigDecimal("8999.00"), 1, "SHIPPED"));
        createOrder(new Order(null, 2L, "AirPods Pro 2", new BigDecimal("1999.00"), 2, "PENDING"));
        createOrder(new Order(null, 3L, "iPad Air", new BigDecimal("4799.00"), 1, "COMPLETED"));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orderStore.values());
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderStore.get(id));
    }

    public List<Order> findByUserId(Long userId) {
        return orderStore.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .toList();
    }

    public Order createOrder(Order order) {
        long id = idGenerator.getAndIncrement();
        order.setId(id);
        order.setOrderTime(LocalDateTime.now());
        orderStore.put(id, order);
        return order;
    }

    public boolean deleteOrder(Long id) {
        return orderStore.remove(id) != null;
    }
}
