package com.example.modulith.advanced.modules.order.service.internal;

import com.example.modulith.advanced.modules.order.api.OrderModule;
import com.example.modulith.advanced.modules.order.entity.OrderEntity;
import com.example.modulith.advanced.modules.product.api.ProductModule;
import com.example.modulith.advanced.modules.user.api.UserModule;
import com.example.modulith.advanced.shared.dto.OrderDTO;
import com.example.modulith.advanced.shared.dto.ProductDTO;
import com.example.modulith.advanced.shared.dto.UserDTO;
import com.example.modulith.advanced.shared.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 订单模块服务实现 - 位于 internal 包内
 *
 * 【进阶设计】与基础版相比，进阶版有以下改进：
 * 1. service实现类移动到 .internal 包，更明确标识为内部实现
 * 2. 通过 @ApplicationModule 描述允许依赖的模块
 * 3. 事件携带更丰富的上下文信息（orderStatus）
 * 4. 依赖的 UserModule/ProductModule 通过构造器注入（接口）
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderModule {

    // ========== DIP：依赖接口，不依赖实现 ==========
    private final UserModule userModule;
    private final ProductModule productModule;
    private final ApplicationEventPublisher eventPublisher;

    // ========== 模块内部存储 ==========
    private final Map<Long, OrderEntity> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderServiceImpl(UserModule userModule,
                            ProductModule productModule,
                            ApplicationEventPublisher eventPublisher) {
        this.userModule = userModule;
        this.productModule = productModule;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderDTO createOrder(Long userId, Long productId, Integer quantity) {
        // 1. 通过接口验证用户（DIP）
        UserDTO user = userModule.findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        if (!user.getActive()) {
            throw new IllegalStateException("用户已停用: " + userId);
        }

        // 2. 通过接口获取商品信息（DIP）
        ProductDTO product = productModule.findProduct(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + productId));

        // 3. 减少库存
        boolean stockReduced = productModule.reduceStock(productId, quantity);
        if (!stockReduced) {
            throw new IllegalStateException("库存不足: " + product.getName());
        }

        // 4. 计算总价 & 创建订单
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        OrderEntity order = OrderEntity.builder()
                .id(idGenerator.getAndIncrement())
                .userId(userId)
                .userName(user.getName())
                .productId(productId)
                .productName(product.getName())
                .quantity(quantity)
                .totalAmount(totalAmount)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
        orderStore.put(order.getId(), order);
        log.info("[Order] 创建订单: id={}, 用户={}, 商品={}, 数量={}, 金额={}",
                order.getId(), user.getName(), product.getName(), quantity, totalAmount);

        // 5. 发布事件（notification 和 audit 模块监听）
        eventPublisher.publishEvent(OrderEvent.created(
                order.getId(), userId, productId, totalAmount));

        return toDTO(order);
    }

    @Override
    public OrderDTO findOrder(Long id) {
        return Optional.ofNullable(orderStore.get(id)).map(this::toDTO).orElse(null);
    }

    @Override
    public List<OrderDTO> findAllOrders() {
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
        log.info("[Order] 完成订单: id={}", id);

        // 发布完成事件
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
        log.info("[Order] 取消订单: id={}", id);

        // 发布取消事件
        eventPublisher.publishEvent(OrderEvent.cancelled(
                order.getId(), order.getUserId(), order.getProductId(), order.getTotalAmount()));

        return toDTO(order);
    }

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
