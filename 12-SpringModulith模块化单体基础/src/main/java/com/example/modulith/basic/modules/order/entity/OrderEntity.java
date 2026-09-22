package com.example.modulith.basic.modules.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体 - 订单模块内部数据结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String status; // CREATED, COMPLETED, CANCELLED
    private LocalDateTime createdAt;
}
