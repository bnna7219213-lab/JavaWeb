package com.example.modulith.advanced.modules.notification.service.internal;

import com.example.modulith.advanced.shared.event.OrderEvent;
import com.example.modulith.advanced.shared.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通知模块服务实现
 *
 * 【进阶核心演示】Notification模块通过 @EventListener 监听其他模块发布的事件，
 * 实现完全的事件驱动解耦：
 *
 * - 监听 OrderEvent（创建/完成/取消）
 * - 监听 UserEvent（创建/停用）
 *
 * Notification模块不知道 OrderService/UserService 的存在，
 * 它只关心事件类型和事件携带的数据。
 *
 * 这种设计的优势：
 * 1. 零耦合：新模块可以通过监听事件接入，无需修改源模块代码
 * 2. 可扩展：可以随意添加更多监听者，事件发布方无感知
 * 3. 可追踪：所有事件处理集中在监听器，方便监控和审计
 */
@Slf4j
@Service
public class NotificationServiceImpl {

    /**
     * 内存中存储通知记录（用于前端展示）
     */
    private final List<NotificationRecord> notifications = Collections.synchronizedList(new ArrayList<>());

    /**
     * 获取所有通知记录（供前端展示使用）
     */
    public List<NotificationRecord> getNotifications() {
        synchronized (notifications) {
            return new ArrayList<>(notifications);
        }
    }

    // ========== 事件监听器 ==========

    /**
     * 监听订单创建事件
     */
    @EventListener
    public void onOrderCreated(OrderEvent event) {
        if (OrderEvent.ORDER_CREATED.equals(event.getEventType())) {
            String message = String.format("新订单通知：订单 #%d 已创建，金额 ¥%.2f",
                    event.getOrderId(), event.getTotalAmount());
            recordNotification("ORDER_CREATED", message, "通知用户订单已创建");
            log.info("[Notification] 📧 发送订单创建通知: orderId={}, userId={}", event.getOrderId(), event.getUserId());
        }
    }

    /**
     * 监听订单完成事件
     */
    @EventListener
    public void onOrderCompleted(OrderEvent event) {
        if (OrderEvent.ORDER_COMPLETED.equals(event.getEventType())) {
            String message = String.format("订单完成通知：订单 #%d 已完成，金额 ¥%.2f",
                    event.getOrderId(), event.getTotalAmount());
            recordNotification("ORDER_COMPLETED", message, "通知用户订单已完成");
            log.info("[Notification] 📧 发送订单完成通知: orderId={}, userId={}", event.getOrderId(), event.getUserId());
        }
    }

    /**
     * 监听订单取消事件
     */
    @EventListener
    public void onOrderCancelled(OrderEvent event) {
        if (OrderEvent.ORDER_CANCELLED.equals(event.getEventType())) {
            String message = String.format("订单取消通知：订单 #%d 已取消",
                    event.getOrderId());
            recordNotification("ORDER_CANCELLED", message, "通知用户订单已取消");
            log.info("[Notification] 📧 发送订单取消通知: orderId={}, userId={}", event.getOrderId(), event.getUserId());
        }
    }

    /**
     * 监听用户创建事件
     */
    @EventListener
    public void onUserCreated(UserEvent event) {
        if (UserEvent.USER_CREATED.equals(event.getEventType())) {
            String message = String.format("欢迎新用户：%s (ID: %d)", event.getUserName(), event.getUserId());
            recordNotification("USER_CREATED", message, "发送欢迎邮件");
            log.info("[Notification] 📧 发送欢迎通知: userId={}, name={}", event.getUserId(), event.getUserName());
        }
    }

    /**
     * 监听用户停用事件
     */
    @EventListener
    public void onUserDeactivated(UserEvent event) {
        if (UserEvent.USER_DEACTIVATED.equals(event.getEventType())) {
            String message = String.format("账户状态通知：用户 %s (ID: %d) 已被停用",
                    event.getUserName(), event.getUserId());
            recordNotification("USER_DEACTIVATED", message, "通知用户账户已停用");
            log.info("[Notification] 📧 发送账户停用通知: userId={}, name={}", event.getUserId(), event.getUserName());
        }
    }

    // ========== 内部方法 ==========

    private void recordNotification(String type, String message, String action) {
        notifications.add(NotificationRecord.builder()
                .type(type)
                .message(message)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * 通知记录 - 内部类
     */
    @lombok.Data
    @lombok.Builder
    public static class NotificationRecord {
        private String type;
        private String message;
        private String action;
        private LocalDateTime timestamp;
    }
}
