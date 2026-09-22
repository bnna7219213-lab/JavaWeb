package com.example.modulith.basic.modules.order.api;

import com.example.modulith.basic.shared.dto.OrderDTO;

import java.util.List;

/**
 * OrderModule接口 - 订单模块对外暴露的API
 *
 * 订单模块是上游模块，它通过UserModule和ProductModule接口
 * 调用用户和商品模块的功能，实现模块间的通信。
 *
 * 核心设计：order模块依赖的是接口（UserModule/ProductModule），
 * 而非实现类，保证了模块间的松耦合。
 */
public interface OrderModule {

    /**
     * 创建订单（跨模块操作：需要查询用户和商品信息）
     */
    OrderDTO createOrder(Long userId, Long productId, Integer quantity);

    /**
     * 根据ID获取订单
     */
    OrderDTO getOrder(Long id);

    /**
     * 列出所有订单
     */
    List<OrderDTO> listOrders();

    /**
     * 完成订单
     */
    OrderDTO completeOrder(Long id);

    /**
     * 取消订单
     */
    OrderDTO cancelOrder(Long id);
}
