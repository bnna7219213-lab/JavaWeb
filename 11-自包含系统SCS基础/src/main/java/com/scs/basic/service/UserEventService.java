package com.scs.basic.service;

import com.scs.basic.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户事件服务 - 演示SCS内的消息发布/订阅
 *
 * <p>使用Spring ApplicationEvent来模拟消息中间件的行为。
 * 在真正的SCS架构中，这里应该是Kafka/RabbitMQ的消息发布和消费。</p>
 *
 * <p>核心演示：</p>
 * <ul>
 *   <li>用户创建后自动发布UserCreated事件</li>
 *   <li>事件监听器异步处理后续逻辑</li>
 *   <li>与MessageService配合，模拟完整的消息流转</li>
 * </ul>
 */
@Service
public class UserEventService {

    private static final Logger log = LoggerFactory.getLogger(UserEventService.class);

    /** Topic常量 - 模拟消息中间件的Topic */
    public static final String TOPIC_USER_CREATED = "scs.user.created";
    public static final String TOPIC_USER_UPDATED = "scs.user.updated";
    public static final String TOPIC_USER_DELETED = "scs.user.deleted";

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MessageService messageService;

    /**
     * 发布用户创建事件
     * 模拟SCS通过消息中间件发布事件给其他系统
     */
    public void publishUserCreatedEvent(User user) {
        // 1. 发布Spring ApplicationEvent (进程内)
        UserCreatedEvent event = new UserCreatedEvent(this, user.getUserId(),
                user.getUsername(), user.getEmail(), user.getCreatedAt().toString());
        eventPublisher.publishEvent(event);

        // 2. 同时写入Message Broker (模拟发送到消息中间件)
        String payload = String.format("{\"userId\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}",
                user.getUserId(), user.getUsername(), user.getEmail());
        messageService.publishMessage(TOPIC_USER_CREATED, "UserCreated", payload);

        log.info("[User SCS] UserCreated事件已发布: userId={}", user.getUserId());
    }

    /**
     * 发布用户更新事件
     */
    public void publishUserUpdatedEvent(User user) {
        UserUpdatedEvent event = new UserUpdatedEvent(this, user.getUserId(),
                user.getUsername(), user.getEmail());
        eventPublisher.publishEvent(event);

        String payload = String.format("{\"userId\":\"%s\",\"username\":\"%s\"}",
                user.getUserId(), user.getUsername());
        messageService.publishMessage(TOPIC_USER_UPDATED, "UserUpdated", payload);

        log.info("[User SCS] UserUpdated事件已发布: userId={}", user.getUserId());
    }

    /**
     * 发布用户删除事件
     */
    public void publishUserDeletedEvent(String userId, String username) {
        UserDeletedEvent event = new UserDeletedEvent(this, userId, username);
        eventPublisher.publishEvent(event);

        String payload = String.format("{\"userId\":\"%s\",\"username\":\"%s\"}", userId, username);
        messageService.publishMessage(TOPIC_USER_DELETED, "UserDeleted", payload);

        log.info("[User SCS] UserDeleted事件已发布: userId={}", userId);
    }

    /**
     * 监听UserCreated事件 - 模拟消息消费者
     * 在进阶版中，这会由另一个独立的SCS来消费
     */
    @EventListener
    public void onUserCreated(UserCreatedEvent event) {
        log.info("[Event Consumer] 收到UserCreated事件: userId={}, username={}",
                event.getUserId(), event.getUsername());
        // 这里可以执行：发送欢迎邮件、初始化用户配置等
    }

    /**
     * 监听UserUpdated事件
     */
    @EventListener
    public void onUserUpdated(UserUpdatedEvent event) {
        log.info("[Event Consumer] 收到UserUpdated事件: userId={}", event.getUserId());
    }

    /**
     * 监听UserDeleted事件
     */
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {
        log.info("[Event Consumer] 收到UserDeleted事件: userId={}", event.getUserId());
    }

    // ===== 内部类：领域事件定义 =====

    /**
     * 用户创建事件
     */
    public static class UserCreatedEvent {
        private final Object source;
        private final String userId;
        private final String username;
        private final String email;
        private final String timestamp;

        public UserCreatedEvent(Object source, String userId, String username,
                                String email, String timestamp) {
            this.source = source;
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.timestamp = timestamp;
        }

        public Object getSource() { return source; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getTimestamp() { return timestamp; }
    }

    /**
     * 用户更新事件
     */
    public static class UserUpdatedEvent {
        private final Object source;
        private final String userId;
        private final String username;
        private final String email;

        public UserUpdatedEvent(Object source, String userId, String username, String email) {
            this.source = source;
            this.userId = userId;
            this.username = username;
            this.email = email;
        }

        public Object getSource() { return source; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }

    /**
     * 用户删除事件
     */
    public static class UserDeletedEvent {
        private final Object source;
        private final String userId;
        private final String username;

        public UserDeletedEvent(Object source, String userId, String username) {
            this.source = source;
            this.userId = userId;
            this.username = username;
        }

        public Object getSource() { return source; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
    }
}
