package com.scs.advanced.event;

import org.springframework.context.ApplicationEvent;

/**
 * UserCreated 领域事件
 *
 * <p>当 User SCS 中创建新用户时发布此事件。
 * Order SCS 监听此事件，为关联用户创建默认订单数据。</p>
 *
 * <p>在实际部署中，此事件通过消息中间件（如Kafka）传递，
 * 这里使用 Spring ApplicationEvent 模拟跨SCS异步通信。</p>
 */
public class UserCreatedEvent extends ApplicationEvent {

    private final String userId;
    private final String username;
    private final String email;
    private final long timestamp;

    public UserCreatedEvent(Object source, String userId, String username, String email) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", source=" + getSource().getClass().getSimpleName() +
                '}';
    }
}
