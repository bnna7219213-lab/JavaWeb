package com.example.modulith.advanced.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单领域事件 - Order模块发布
 * Notification模块和Audit模块监听此事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderEvent extends DomainEvent {
    private Long orderId;
    private Long userId;
    private Long productId;
    private BigDecimal totalAmount;
    private String orderStatus;

    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String ORDER_COMPLETED = "ORDER_COMPLETED";
    public static final String ORDER_CANCELLED = "ORDER_CANCELLED";

    public OrderEvent(String eventType, Long orderId, Long userId, Long productId, BigDecimal totalAmount, String orderStatus) {
        super(eventType);
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public static OrderEvent created(Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        return new OrderEvent(ORDER_CREATED, orderId, userId, productId, totalAmount, "CREATED");
    }

    public static OrderEvent completed(Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        return new OrderEvent(ORDER_COMPLETED, orderId, userId, productId, totalAmount, "COMPLETED");
    }

    public static OrderEvent cancelled(Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        return new OrderEvent(ORDER_CANCELLED, orderId, userId, productId, totalAmount, "CANCELLED");
    }
}
