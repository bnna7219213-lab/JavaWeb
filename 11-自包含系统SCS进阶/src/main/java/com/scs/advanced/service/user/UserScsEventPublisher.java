package com.scs.advanced.service.user;

import com.scs.advanced.entity.user.ScsUser;
import com.scs.advanced.event.UserCreatedEvent;
import com.scs.advanced.event.UserDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 *	User SCS 事件发布器
 *
 * <p>这是User SCS发布领域事件的服务。
 * 在实际SCS架构中，这里会将事件发布到Kafka等消息中间件，
 * 让其他SCS（如Order SCS）可以订阅。</p>
 *
 * <p>演示流程：</p>
 * <pre>
 *   用户注册 -> userScsEventPublisher.publishUserCreatedEvent(user)
 *           -> Spring ApplicationEvent (模拟 Kafka publish)
 *           -> Order SCS @EventListener 接收到事件
 *           -> orderScsService.createDefaultOrder(userId, username)
 * </pre>
 */
@Service
public class UserScsEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserScsEventPublisher.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发布 UserCreated 事件
     * 模拟SCS通过消息中间件发布事件
     *
     * @param user 新创建的用户
     */
    public void publishUserCreatedEvent(ScsUser user) {
        UserCreatedEvent event = new UserCreatedEvent(
                this,
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );

        // 发布事件 - 在实际SCS中，这会将事件发送到Kafka Topic: "scs.events.user.created"
        eventPublisher.publishEvent(event);

        log.info("[User SCS] 已发布 UserCreated 事件 -> 用户: {} ({}), 目标: 其他SCS订阅者",
                user.getUsername(), user.getUserId());
        log.info("[User SCS] (模拟) 消息已投递到 Topic: scs.events.user.created");
    }

    /**
     * 发布 UserDeleted 事件
     *
     * @param userId   被删除的用户ID
     * @param username 被删除的用户名
     */
    public void publishUserDeletedEvent(String userId, String username) {
        UserDeletedEvent event = new UserDeletedEvent(this, userId, username);

        eventPublisher.publishEvent(event);

        log.info("[User SCS] 已发布 UserDeleted 事件 -> 用户: {} ({})",
                username, userId);
        log.info("[User SCS] (模拟) 消息已投递到 Topic: scs.events.user.deleted");
    }
}
