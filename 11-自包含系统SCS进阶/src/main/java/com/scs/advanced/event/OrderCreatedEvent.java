package com.scs.advanced.event;

import org.springframework.context.ApplicationEvent;

/**
 * OrderCreated 领域事件
 *
 * <p>当 Order SCS 中创建订单时发布此事件。
 * 其他SCS可以监听此事件进行后续处理。</p>
 */
public class OrderCreatedEvent extends ApplicationEvent {

    private final String orderId;
    private final String userId;
    private final String orderType;
    private final long timestamp;

    public OrderCreatedEvent(Object source, String orderId, String userId, String orderType) {
        super(source);
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.timestamp = System.currentTimeMillis();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderType() {
        return orderType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}
