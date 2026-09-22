package com.example.modulith.basic.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户DTO - 跨模块共享数据对象
 * 模块间通过DTO传递数据，避免直接暴露实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Boolean active;
}
