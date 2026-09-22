package com.example.graalvm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Entity - Advanced Version
 *
 * Native Image considerations:
 * - BigDecimal: needs reflection registration for Jackson
 * - LocalDateTime: needs JSR-310 module registration
 * - List<String>: needs parameterized type hints
 * - All nested enum types need registration
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String orderNumber;
    private List<String> productNames;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private Integer quantity;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String shippingAddress;
    private LocalDateTime orderTime;
    private LocalDateTime shipTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Order Status Enum - must be registered in RuntimeHints
     * for switch-based operations in native image.
     */
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }

    /**
     * Payment Method Enum
     */
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, ALIPAY, WECHAT_PAY, BANK_TRANSFER, CASH_ON_DELIVERY
    }

    public Order() {
        // Required for GraalVM reflection-based deserialization
    }

    public Order(Long id, Long userId, String orderNumber, BigDecimal totalAmount,
                 Integer quantity, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.quantity = quantity;
        this.status = status;
        this.orderTime = LocalDateTime.now();
        this.createdAt = this.orderTime;
        this.updatedAt = this.createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public List<String> getProductNames() { return productNames; }
    public void setProductNames(List<String> productNames) { this.productNames = productNames; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getShippingAmount() { return shippingAmount; }
    public void setShippingAmount(BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getShipTime() { return shipTime; }
    public void setShipTime(LocalDateTime shipTime) { this.shipTime = shipTime; }

    public LocalDateTime getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(LocalDateTime deliveryTime) { this.deliveryTime = deliveryTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", orderNumber='" + orderNumber + "', amount=" + totalAmount + ", status=" + status + "}";
    }
}
