package com.aether.bff.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单实体（含前端友好的状态文本转换）
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String status;
    private String statusText;
    private String statusColor;
    private Double totalAmount;
    private String createTime;
    private List<OrderItem> items;

    public Order() {
    }

    public Order(String orderId, String status, Double totalAmount,
                 LocalDateTime createTime, List<OrderItem> items) {
        this.orderId = orderId;
        this.status = status;
        this.statusText = convertStatus(status);
        this.statusColor = convertStatusColor(status);
        this.totalAmount = totalAmount;
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.items = items;
    }

    private String convertStatus(String status) {
        switch (status) {
            case "PAID": return "已支付";
            case "SHIPPED": return "已发货";
            case "DELIVERED": return "已签收";
            case "PENDING": return "待付款";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }

    private String convertStatusColor(String status) {
        switch (status) {
            case "PAID": return "#3b82f6";
            case "SHIPPED": return "#8b5cf6";
            case "DELIVERED": return "#10b981";
            case "PENDING": return "#f59e0b";
            case "CANCELLED": return "#ef4444";
            default: return "#64748b";
        }
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    /**
     * 订单商品子项
     */
    public static class OrderItem implements Serializable {
        private String productId;
        private String productName;
        private String image;
        private Integer quantity;
        private Double price;

        public OrderItem() { }

        public OrderItem(String productId, String productName, String image, Integer quantity, Double price) {
            this.productId = productId;
            this.productName = productName;
            this.image = image;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}
