package com.example.hilla.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单实体
 * 对应 TypeScript Order 接口（由 Hilla 自动生成）
 */
public class Order {

    private Long id;
    private Integer userId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String status;
    private String createdAt;

    public Order() {}

    public Order(Long id, Integer userId, String productName, Integer quantity, BigDecimal price, String status) {
        this.id = id;
        this.userId = userId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", product='" + productName + "', qty=" + quantity
                + ", price=" + price + ", status='" + status + "'}";
    }
}
