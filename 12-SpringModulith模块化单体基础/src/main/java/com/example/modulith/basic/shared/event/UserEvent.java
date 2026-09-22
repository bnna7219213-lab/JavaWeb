package com.example.modulith.basic.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户领域事件 - 用于模块间事件通知
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserEvent extends DomainEvent {

    private Long userId;
    private String userName;

    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_DEACTIVATED = "USER_DEACTIVATED";

    public UserEvent(String eventType, Long userId, String userName) {
        super(eventType);
        this.userId = userId;
        this.userName = userName;
    }

    public static UserEvent created(Long userId, String userName) {
        return new UserEvent(USER_CREATED, userId, userName);
    }

    public static UserEvent deactivated(Long userId, String userName) {
        return new UserEvent(USER_DEACTIVATED, userId, userName);
    }
}
