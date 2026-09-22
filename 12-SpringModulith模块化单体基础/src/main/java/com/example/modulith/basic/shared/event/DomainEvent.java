package com.example.modulith.basic.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 领域事件基类 - 模块间事件通信的抽象
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
