package com.example.modulith.basic.modules.order.service;

import com.example.modulith.basic.modules.order.api.OrderModule;
import com.example.modulith.basic.modules.order.entity.OrderEntity;
import com.example.modulith.basic.modules.product.api.ProductModule;
import com.example.modulith.basic.modules.user.api.UserModule;
import com.example.modulith.basic.shared.dto.OrderDTO;
import com.example.modulith.basic.shared.dto.ProductDTO;
import com.example.modulith.basic.shared.dto.UserDTO;
import com.example.modulith.basic.shared.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 订单模块服务实现
 *
 * 【关键设计】本类通过构造器注入UserModule和ProductModule接口，
 * 而不是注入具体的实现类（UserService/ProductService）。
 *
 * 这实现了依赖倒置原则（DIP）：
 * - 高层模块（order）和低层模块（user/product）都依赖于抽象（接口）
 * - order模块不需要知道user/product模块的内部实现细节
 *
 * 如果将来需要替换user模块的实现（如远程服务、不同存储），
 * order模块的代码无需修改。
 */
@Slf4j
@Service
public class OrderService implements OrderModule {

    // ========== 模块间接口依赖（依赖倒置）==========
    private final UserModule userModule;
    private final ProductModule productModule;
    private final ApplicationEventPublisher eventPublisher;

    // ========== 模块内部存储 ==========
    private final Map<Long, OrderEntity> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 构造器注入 - 依赖UserModule和ProductModule接口
     */
    public OrderService(UserModule userModule,
                        ProductModule productModule,
                        ApplicationEventPublisher eventPublisher) {
        this.userModule = userModule;
        this.productModule = productModule;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderDTO createOrder(Long userId, Long productId, Integer quantity) {
        // 1. 通过UserModule接口验证用户（不依赖UserService实现）
        UserDTO user = userModule.getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + userId);
        }
        if (!user.getActive()) {
            throw new IllegalStateException("用户已停用: " + userId);
        }

        // 2. 通过ProductModule接口获取商品信息（不依赖ProductService实现）
        ProductDTO product = productModule.getProduct(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在: " + productId);
        }

        // 3. 减少库存
        boolean stockReduced = productModule.reduceStock(productId, quantity);
        if (!stockReduced) {
            throw new IllegalStateException("库存不足: " + product.getName());
        }

        // 4. 计算总价
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        // 5. 创建订单
        OrderEntity order = OrderEntity.builder()
                .id(idGenerator.getAndIncrement())
                .userId(userId)
                .userName(user.getName())
                .productId(productId)
                .productName(product.getName())
                .quantity(quantity)
                .totalAmount(totalAmount)
                .status("CREATED")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        orderStore.put(order.getId(), order);
        log.info("创建订单: id={}, 用户={}, 商品={}, 数量={}, 总金额={}",
                order.getId(), user.getName(), product.getName(), quantity, totalAmount);

        // 6. 发布订单创建事件（其他模块可以监听此事件）
        eventPublisher.publishEvent(OrderEvent.created(
                order.getId(), userId, productId, totalAmount));

        return toDTO(order);
    }

    @Override
    public OrderDTO getOrder(Long id) {
        OrderEntity entity = orderStore.get(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    public List<OrderDTO> listOrders() {
        return orderStore.values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO completeOrder(Long id) {
        OrderEntity order = orderStore.get(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在: " + id);
        }
        order.setStatus("COMPLETED");
        log.info("完成订单: id={}", id);

        // 发布订单完成事件
        eventPublisher.publishEvent(OrderEvent.completed(
                order.getId(), order.getUserId(), order.getProductId(), order.getTotalAmount()));

        return toDTO(order);
    }

    @Override
    public OrderDTO cancelOrder(Long id) {
        OrderEntity order = orderStore.get(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在: " + id);
        }
        order.setStatus("CANCELLED");
        log.info("取消订单: id={}", id);

        // 发布订单取消事件
        eventPublisher.publishEvent(OrderEvent.cancelled(
                order.getId(), order.getUserId(), order.getProductId(), order.getTotalAmount()));

        return toDTO(order);
    }

    /**
     * Entity转换为DTO - 内部方法
     */
    private OrderDTO toDTO(OrderEntity entity) {
        return OrderDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
