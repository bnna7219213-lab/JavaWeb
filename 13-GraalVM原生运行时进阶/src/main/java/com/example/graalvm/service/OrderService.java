package com.example.graalvm.service;

import com.example.graalvm.entity.Order;
import com.example.graalvm.entity.Order.OrderStatus;
import com.example.graalvm.entity.Order.PaymentMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Order Service - Advanced with full entity relationships
 */
@Service
public class OrderService {

    private final Map<Long, Order> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderService() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        createSampleOrder(1L, 1L, "ORD-202401001",
                new BigDecimal("24999.00"), 1, OrderStatus.DELIVERED, PaymentMethod.ALIPAY);
        createSampleOrder(2L, 1L, "ORD-202401002",
                new BigDecimal("8999.00"), 1, OrderStatus.SHIPPED, PaymentMethod.WECHAT_PAY);
        createSampleOrder(3L, 1L, "ORD-202401003",
                new BigDecimal("1999.00"), 2, OrderStatus.PROCESSING, PaymentMethod.CREDIT_CARD);
        createSampleOrder(4L, 2L, "ORD-202401004",
                new BigDecimal("4799.00"), 1, OrderStatus.DELIVERED, PaymentMethod.ALIPAY);
        createSampleOrder(5L, 2L, "ORD-202401005",
                new BigDecimal("12999.00"), 1, OrderStatus.PENDING, PaymentMethod.BANK_TRANSFER);
        createSampleOrder(6L, 3L, "ORD-202401006",
                new BigDecimal("699.00"), 3, OrderStatus.CONFIRMED, PaymentMethod.CREDIT_CARD);
        createSampleOrder(7L, 4L, "ORD-202401007",
                new BigDecimal("3599.00"), 1, OrderStatus.SHIPPED, PaymentMethod.WECHAT_PAY);
        createSampleOrder(8L, 4L, "ORD-202401008",
                new BigDecimal("15999.00"), 1, OrderStatus.CANCELLED, PaymentMethod.DEBIT_CARD);

        idGenerator.set(9);
    }

    private void createSampleOrder(long id, long userId, String orderNumber,
                                   BigDecimal amount, int quantity,
                                   OrderStatus status, PaymentMethod payment) {
        Order order = new Order(id, userId, orderNumber, amount, quantity, status);
        order.setPaymentMethod(payment);
        order.setTaxAmount(amount.multiply(new BigDecimal("0.13")));
        order.setShippingAmount(new BigDecimal("15.00"));
        order.setTotalAmount(amount.add(order.getTaxAmount()).add(order.getShippingAmount()));
        order.setOrderTime(LocalDateTime.now().minusDays(id));
        order.setCreatedAt(order.getOrderTime());
        if (status == OrderStatus.DELIVERED || status == OrderStatus.SHIPPED) {
            order.setShipTime(order.getOrderTime().plusDays(1));
        }
        if (status == OrderStatus.DELIVERED) {
            order.setDeliveryTime(order.getOrderTime().plusDays(3));
        }
        orderStore.put(id, order);
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

    public List<Order> findByStatus(OrderStatus status) {
        return orderStore.values().stream()
                .filter(o -> o.getStatus() == status)
                .toList();
    }

    public Order createOrder(Order order) {
        long id = idGenerator.getAndIncrement();
        order.setId(id);
        if (order.getOrderNumber() == null) {
            order.setOrderNumber("ORD-" + System.currentTimeMillis());
        }
        order.setOrderTime(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }
        orderStore.put(id, order);
        return order;
    }

    public boolean deleteOrder(Long id) {
        return orderStore.remove(id) != null;
    }

    public long count() {
        return orderStore.size();
    }

    public Map<OrderStatus, Long> getStatusDistribution() {
        Map<OrderStatus, Long> dist = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            dist.put(s, 0L);
        }
        orderStore.values().forEach(o -> {
            dist.merge(o.getStatus(), 1L, Long::sum);
        });
        return dist;
    }
}
