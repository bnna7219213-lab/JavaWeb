package com.example.modulith.basic.modules.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体 - 用户模块内部数据结构
 *
 * 注意：此类位于entity包下，属于模块内部实现细节，
 * 其他模块不应直接引用此类。对外交互应通过UserDTO和UserModule接口。
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
}
