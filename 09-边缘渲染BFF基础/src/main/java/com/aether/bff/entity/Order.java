package com.aether.bff.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单实体
 *
 * <p>模拟从"订单微服务"获取的订单信息。
 * BFF层会对订单状态做前端友好的转换。</p>
 */
public class Order {

    private String orderId;
    private String status;
    private String statusText;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusText = convertStatus(status);
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * 订单商品子项
     */
    public static class OrderItem {
        private String productId;
        private String productName;
        private String image;
        private Integer quantity;
        private Double price;

        public OrderItem() {
        }

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
