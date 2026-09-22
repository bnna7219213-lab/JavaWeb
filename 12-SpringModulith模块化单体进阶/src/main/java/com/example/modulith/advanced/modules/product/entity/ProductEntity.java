package com.example.modulith.advanced.modules.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品实体 - 商品模块内部数据结构（不对外暴露）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private java.time.LocalDateTime createdAt;
}
