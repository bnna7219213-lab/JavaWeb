package com.example.modulith.advanced.modules.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体 - 用户模块内部数据结构（不对外暴露）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private Long id;
    private String name;
    private String email;
    private Boolean active;
    private java.time.LocalDateTime createdAt;
}
