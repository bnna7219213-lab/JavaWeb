package com.example.service;

import com.example.entity.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final List<Order> orders = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderService() {
        orders.add(new Order(idGenerator.getAndIncrement(), "ORD2024001", 1L, "MacBook Pro", new BigDecimal("14999.00"), "已支付"));
        orders.add(new Order(idGenerator.getAndIncrement(), "ORD2024002", 1L, "iPhone 15", new BigDecimal("6999.00"), "待发货"));
        orders.add(new Order(idGenerator.getAndIncrement(), "ORD2024003", 2L, "AirPods Pro", new BigDecimal("1899.00"), "已完成"));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    public List<Order> findByUserId(Long userId) {
        return orders.stream().filter(o -> o.getUserId().equals(userId)).toList();
    }

    public Optional<Order> findById(Long id) {
        return orders.stream().filter(o -> o.getId().equals(id)).findFirst();
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
            order.setOrderNo("ORD" + System.currentTimeMillis());
            orders.add(order);
        } else {
            deleteById(order.getId());
            orders.add(order);
        }
        return order;
    }

    public boolean deleteById(Long id) {
        return orders.removeIf(o -> o.getId().equals(id));
    }

    public int count() {
        return orders.size();
    }

    public Order updateStatus(Long id, String status) {
        Optional<Order> opt = findById(id);
        if (opt.isPresent()) {
            Order order = opt.get();
            order.setStatus(status);
            return order;
        }
        return null;
    }
}
