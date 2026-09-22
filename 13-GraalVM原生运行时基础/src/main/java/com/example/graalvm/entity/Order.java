package com.example.graalvm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * GraalVM Native Image 注意事项:
 * - BigDecimal的序列化需要特殊注册（如果涉及反射）
 * - LocalDateTime需要JSR-310模块注册
 * - 所有字段必须通过getter/setter暴露给反射
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String productName;
    private BigDecimal amount;
    private Integer quantity;
    private String status;
    private LocalDateTime orderTime;

    public Order() {
        // 默认构造器 - GraalVM Native Image需要
    }

    public Order(Long id, Long userId, String productName, BigDecimal amount, Integer quantity, String status) {
        this.id = id;
        this.userId = userId;
        this.productName = productName;
        this.amount = amount;
        this.quantity = quantity;
        this.status = status;
        this.orderTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", product='" + productName + "', amount=" + amount + ", status='" + status + "'}";
    }
}
