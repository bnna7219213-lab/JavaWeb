package com.example.modulith.advanced.modules.order.api;

import com.example.modulith.advanced.shared.dto.OrderDTO;

import java.util.List;

/**
 * OrderModule接口 - 订单模块对外暴露的API
 * 
 * 允许依赖：user, product
 * 订单模块通过 DIP 引用 user 和 product 模块，不直接耦合其实现。
 */
public interface OrderModule {

    OrderDTO createOrder(Long userId, Long productId, Integer quantity);

    OrderDTO findOrder(Long id);

    List<OrderDTO> findAllOrders();

    OrderDTO completeOrder(Long id);

    OrderDTO cancelOrder(Long id);
}
