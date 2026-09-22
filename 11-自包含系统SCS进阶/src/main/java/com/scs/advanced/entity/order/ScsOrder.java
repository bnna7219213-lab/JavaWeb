package com.scs.advanced.entity.order;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SCS订单实体 - Order SCS的领域模型
 *
 * <p>注意：Order SCS的订单实体有自己的定义，
 * 它只存储自己需要的数据，绝不依赖User SCS的内部数据结构。</p>
 *
 * <p>Order SCS通过事件中传递的userId来关联用户，
 * 但不直接访问User SCS的数据库。</p>
 */
public class ScsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String userId;      // 关联的用户ID (来自User SCS)
    private String username;     // 冗余的用户名 (事件中获取，避免跨SCS查询)
    private String orderType;    // 订单类型: DEFAULT(默认创建), MANUAL(手动创建)
    private String description;  // 订单描述
    private OrderStatus status;
    private LocalDateTime createdAt;

    public enum OrderStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    public ScsOrder() {
        this.orderId = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public ScsOrder(String userId, String username, String orderType, String description) {
        this();
        this.userId = userId;
        this.username = username;
        this.orderType = orderType;
        this.description = description;
    }

    // ===== Getters & Setters =====

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ScsOrder{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", orderType='" + orderType + '\'' +
                ", status=" + status +
                '}';
    }
}
