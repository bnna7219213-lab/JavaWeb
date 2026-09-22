package com.example.modulith.advanced.modules.audit.service.internal;

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
 * 审计模块服务实现
 *
 * 【进阶核心演示】Audit模块监听所有业务事件，记录完整的操作审计日志。
 *
 * 审计模块与通知模块类似，通过事件驱动工作：
 * - 监听 OrderEvent（所有类型）
 * - 监听 UserEvent（所有类型）
 *
 * 关键区别：审计模块通常不直接依赖其他模块的API，
 * 它只需要事件数据来构建审计记录，这体现了纯事件驱动通信的优雅。
 *
 * 优势：添加审计功能时，无需修改任何业务模块的代码。
 */
@Slf4j
@Service
public class AuditServiceImpl {

    /**
     * 审计日志存储
     */
    private final List<AuditRecord> auditLogs = Collections.synchronizedList(new ArrayList<>());

    public List<AuditRecord> getAuditLogs() {
        synchronized (auditLogs) {
            return new ArrayList<>(auditLogs);
        }
    }

    // ========== 事件监听器 ==========

    @EventListener
    public void onOrderEvent(OrderEvent event) {
        String detail = String.format("订单事件 [orderId=%d, userId=%d, productId=%d, amount=¥%.2f, status=%s]",
                event.getOrderId(), event.getUserId(), event.getProductId(),
                event.getTotalAmount(), event.getOrderStatus());

        recordAudit("ORDER", event.getEventType(), detail);
        log.info("[Audit] 📝 审计记录: {} - {}", event.getEventType(), detail);
    }

    @EventListener
    public void onUserEvent(UserEvent event) {
        String detail = String.format("用户事件 [userId=%d, userName=%s]",
                event.getUserId(), event.getUserName());

        recordAudit("USER", event.getEventType(), detail);
        log.info("[Audit] 📝 审计记录: {} - {}", event.getEventType(), detail);
    }

    // ========== 内部方法 ==========

    private void recordAudit(String category, String action, String detail) {
        auditLogs.add(AuditRecord.builder()
                .category(category)
                .action(action)
                .detail(detail)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * 审计记录
     */
    @lombok.Data
    @lombok.Builder
    public static class AuditRecord {
        private String category;
        private String action;
        private String detail;
        private LocalDateTime timestamp;
    }
}
