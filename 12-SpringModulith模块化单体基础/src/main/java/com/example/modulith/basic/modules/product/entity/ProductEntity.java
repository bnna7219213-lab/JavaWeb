package com.example.modulith.basic.modules.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品实体 - 商品模块内部数据结构
 *
 * 属于模块内部实现细节，不允许外部模块直接引用。
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
}
