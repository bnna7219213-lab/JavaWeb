package com.example.modulith.basic.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单领域事件 - 订单模块发布事件，其他模块可监听
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

    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String ORDER_COMPLETED = "ORDER_COMPLETED";
    public static final String ORDER_CANCELLED = "ORDER_CANCELLED";

    public OrderEvent(String eventType, Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        super(eventType);
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.totalAmount = totalAmount;
    }

    public static OrderEvent created(Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        return new OrderEvent(ORDER_CREATED, orderId, userId, productId, totalAmount);
    }

    public static OrderEvent completed(Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        return new OrderEvent(ORDER_COMPLETED, orderId, userId, productId, totalAmount);
    }

    public static OrderEvent cancelled(Long orderId, Long userId, Long productId, BigDecimal totalAmount) {
        return new OrderEvent(ORDER_CANCELLED, orderId, userId, productId, totalAmount);
    }
}
