package com.scs.advanced.event;

import org.springframework.context.ApplicationEvent;

/**
 * UserDeleted 领域事件
 *
 * <p>当 User SCS 中删除用户时发布此事件。
 * Order SCS 监听此事件，处理关联订单数据。</p>
 */
public class UserDeletedEvent extends ApplicationEvent {

    private final String userId;
    private final String username;
    private final long timestamp;

    public UserDeletedEvent(Object source, String userId, String username) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "UserDeletedEvent{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
