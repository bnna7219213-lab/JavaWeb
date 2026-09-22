package com.example.modulith.advanced.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 领域事件基类 - 所有模块事件的统一抽象
 *
 * 事件驱动通信是 Modulith 的核心机制之一：
 * - 源模块发布事件（不关心谁监听）
 * - 监听模块通过 @EventListener 接收事件
 * - 实现模块间的松耦合通信
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent {
    private String eventType;
    private LocalDateTime occurredAt;

    protected DomainEvent(String eventType) {
        this.eventType = eventType;
        this.occurredAt = LocalDateTime.now();
    }
}
