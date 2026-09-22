package com.scs.advanced.service.order;

import com.scs.advanced.entity.order.ScsOrder;
import com.scs.advanced.entity.order.ScsOrder.OrderStatus;
import com.scs.advanced.event.UserCreatedEvent;
import com.scs.advanced.event.UserDeletedEvent;
import com.scs.advanced.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Order SCS 业务服务
 *
 * <p>管理订单的完整生命周期。这是 Order SCS 的核心业务逻辑。</p>
 *
 * <p>数据存储使用前缀 "ORDER_SCS_DB" 的 ConcurrentHashMap。</p>
 *
 * <p>跨SCS通信核心演示：</p>
 * <ul>
 *   <li>@EventListener(UserCreatedEvent) - 监听User SCS发布的事件</li>
 *   <li>自动为新用户创建默认订单数据</li>
 *   <li>发布自己的OrderCreated事件供其他SCS消费</li>
 * </ul>
 */
@Service
public class ScsOrderService {

    private static final Logger log = LoggerFactory.getLogger(ScsOrderService.class);

    /**
     * Order SCS 的独立数据库模拟
     * 使用前缀 "ORDER_SCS_DB" - 与 User SCS 的 "USER_SCS_DB" 完全隔离
     */
    private final Map<String, ScsOrder> orderDatabase = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // ===== 核心业务方法 =====

    /**
     * 创建订单
     */
    public ScsOrder createOrder(String userId, String username, String description) {
        ScsOrder order = new ScsOrder(userId, username, "MANUAL", description);
        orderDatabase.put(order.getOrderId(), order);

        log.info("[Order SCS] 订单创建成功: orderId={}, userId={}", order.getOrderId(), userId);

        // 发布OrderCreated事件
        eventPublisher.publishEvent(new OrderCreatedEvent(
                this, order.getOrderId(), userId, order.getOrderType()));

        return order;
    }

    /**
     * 创建默认订单 - 当新用户注册时自动调用
     * 这是跨SCS异步通信的核心：用户SCS发布事件 -> 订单SCS消费 -> 创建默认订单
     */
    public ScsOrder createDefaultOrder(String userId, String username) {
        ScsOrder order = new ScsOrder(userId, username, "DEFAULT",
                "新用户欢迎礼包 - 自动创建 (来源: UserCreated事件)");
        order.setStatus(OrderStatus.CONFIRMED);
        orderDatabase.put(order.getOrderId(), order);

        log.info("[Order SCS] 为新用户自动创建默认订单: userId={}, orderId={}",
                userId, order.getOrderId());

        // 发布OrderCreated事件
        eventPublisher.publishEvent(new OrderCreatedEvent(
                this, order.getOrderId(), userId, "DEFAULT"));

        return order;
    }

    /**
     * 根据ID查询订单
     */
    public Optional<ScsOrder> findById(String orderId) {
        return Optional.ofNullable(orderDatabase.get(orderId));
    }

    /**
     * 查询用户的所有订单
     */
    public List<ScsOrder> findByUserId(String userId) {
        return orderDatabase.values().stream()
                .filter(o -> userId.equals(o.getUserId()))
                .sorted(Comparator.comparing(ScsOrder::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 查询所有订单
     */
    public List<ScsOrder> findAll() {
        return orderDatabase.values().stream()
                .sorted(Comparator.comparing(ScsOrder::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 删除用户的订单
     */
    public int deleteUserOrders(String userId) {
        List<String> toRemove = orderDatabase.entrySet().stream()
                .filter(e -> userId.equals(e.getValue().getUserId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        toRemove.forEach(orderDatabase::remove);
        return toRemove.size();
    }

    /**
     * 获取订单总数
     */
    public long count() {
        return orderDatabase.size();
    }

    /**
     * 获取数据库信息
     */
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("databaseName", "ORDER_SCS_DB (Order SCS 专属)");
        info.put("totalRecords", orderDatabase.size());
        info.put("storagePrefix", "ORDER_SCS_DB");
        info.put("isolationLevel", "完全隔离 - 仅Order SCS可访问");
        info.put("scsUnit", "Order Self-Contained System");
        return info;
    }

    // ===== 跨SCS事件监听 =====

    /**
     * 监听 UserCreated 事件 - 跨SCS异步通信的核心
     *
     * <p>当 User SCS 创建新用户时，会发布 UserCreatedEvent。
     * Order SCS 通过 @EventListener 接收此事件，并自动为新用户创建默认订单数据。</p>
     *
     * <p>在实际SCS架构中，这对应 Kafka Consumer 消费 scs.events.user.created Topic。</p>
     */
    @EventListener
    public void onUserCreated(UserCreatedEvent event) {
        log.info("[Order SCS] 收到跨SCS事件: UserCreated - userId={}, username={}, email={}",
                event.getUserId(), event.getUsername(), event.getEmail());
        log.info("[Order SCS] 正在为新用户创建默认订单数据...");

        createDefaultOrder(event.getUserId(), event.getUsername());

        log.info("[Order SCS] 默认订单创建完成，跨SCS协同完成: User SCS -> Order SCS");
    }

    /**
     * 监听 UserDeleted 事件
     *
     * <p>当用户被删除时，清理关联的订单数据。</p>
     */
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {
        log.info("[Order SCS] 收到跨SCS事件: UserDeleted - userId={}, username={}",
                event.getUserId(), event.getUsername());

        int deleted = deleteUserOrders(event.getUserId());
        log.info("[Order SCS] 已清理 {} 个关联订单 (userId={})", deleted, event.getUserId());
    }
}
