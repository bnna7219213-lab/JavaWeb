package com.scs.advanced.messaging;

import com.scs.advanced.event.OrderCreatedEvent;
import com.scs.advanced.event.UserCreatedEvent;
import com.scs.advanced.event.UserDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * SCS 消息中间件模拟
 *
 * <p>模拟Kafka/RabbitMQ等消息中间件，在多个SCS之间传递领域事件。</p>
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>Topic管理 - 模拟消息队列的Topic</li>
 *   <li>消息持久化 - 消息不会因消费者宕机而丢失</li>
 *   <li>发布订阅 - 多个SCS可订阅同一Topic</li>
 *   <li>跨SCS通信 - 模拟分布式消息传递</li>
 * </ul>
 *
 * <p>部署说明：在实际SCS架构中，这是独立运行的消息中间件服务（如Kafka集群），
 * 每个SCS通过消息中间件进行通信。</p>
 */
@Component
public class ScsMessageBroker {

    private static final Logger log = LoggerFactory.getLogger(ScsMessageBroker.class);

    /**
     * Topic存储 - 模拟Kafka Topic
     * Key: Topic名称, Value: 消息列表
     */
    private final Map<String, List<BrokerMessage>> topicStore = new ConcurrentHashMap<>();

    /**
     * 消息计数器
     */
    private final Map<String, Integer> topicCounters = new ConcurrentHashMap<>();

    /**
     * 发布消息到指定Topic
     *
     * @param topic   Topic名称 (如 "scs.events.user.created")
     * @param payload 消息内容
     * @param source 发布方SCS标识
     */
    public void publish(String topic, String payload, String source) {
        BrokerMessage message = new BrokerMessage(topic, payload, source);
        topicStore.computeIfAbsent(topic, k -> Collections.synchronizedList(new ArrayList<>()))
                   .add(message);
        topicCounters.merge(topic, 1, Integer::sum);

        log.info("[SCS Message Broker] 消息发布 | Topic: {} | Source: {} | 内容: {}",
                topic, source, payload.length() > 80 ? payload.substring(0, 80) + "..." : payload);
    }

    /**
     * 消费指定Topic的所有未消费消息
     *
     * @param topic Topic名称
     * @return 未消费的消息列表
     */
    public List<BrokerMessage> consume(String topic) {
        List<BrokerMessage> messages = topicStore.getOrDefault(topic, Collections.emptyList());
        return messages.stream()
                .filter(m -> !m.isConsumed())
                .peek(m -> m.setConsumed(true))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有Topic名称
     */
    public Set<String> getAllTopics() {
        return new TreeSet<>(topicStore.keySet());
    }

    /**
     * 获取Broker整体状态
     */
    public Map<String, Object> getBrokerInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("brokerType", "SCS Distributed Message Broker (模拟Kafka)");
        info.put("totalTopics", topicStore.size());
        info.put("totalMessages", topicStore.values().stream().mapToInt(List::size).sum());
        info.put("topics", topicStore.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Map.of(
                                "total", e.getValue().size(),
                                "unconsumed", e.getValue().stream().filter(m -> !m.isConsumed()).count()
                        )
                )));
        return info;
    }

    /**
     * 获取最近的消息
     */
    public List<BrokerMessage> getRecentMessages(int limit) {
        return topicStore.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(BrokerMessage::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ===== 跨SCS事件路由 =====

    /**
     * 监听UserCreated事件 -> 转发到消息中间件
     * 模拟: 用户SCS发布事件到Kafka
     */
    @EventListener(UserCreatedEvent.class)
    public void routeUserCreatedEvent(UserCreatedEvent event) {
        String payload = String.format("{\"userId\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}",
                event.getUserId(), event.getUsername(), event.getEmail());
        publish("scs.events.user.created", payload, "User SCS");
    }

    /**
     * 监听UserDeleted事件 -> 转发到消息中间件
     */
    @EventListener(UserDeletedEvent.class)
    public void routeUserDeletedEvent(UserDeletedEvent event) {
        String payload = String.format("{\"userId\":\"%s\",\"username\":\"%s\"}",
                event.getUserId(), event.getUsername());
        publish("scs.events.user.deleted", payload, "User SCS");
    }

    /**
     * 监听OrderCreated事件 -> 转发到消息中间件
     */
    @EventListener(OrderCreatedEvent.class)
    public void routeOrderCreatedEvent(OrderCreatedEvent event) {
        String payload = String.format("{\"orderId\":\"%s\",\"userId\":\"%s\",\"type\":\"%s\"}",
                event.getOrderId(), event.getUserId(), event.getOrderType());
        publish("scs.events.order.created", payload, "Order SCS");
    }

    // ===== 内部消息实体 =====

    /**
     * 消息代理内部消息
     */
    public static class BrokerMessage {
        private final String messageId;
        private final String topic;
        private final String payload;
        private final String source;
        private final long timestamp;
        private boolean consumed;

        public BrokerMessage(String topic, String payload, String source) {
            this.messageId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            this.topic = topic;
            this.payload = payload;
            this.source = source;
            this.timestamp = System.currentTimeMillis();
            this.consumed = false;
        }

        public String getMessageId() { return messageId; }
        public String getTopic() { return topic; }
        public String getPayload() { return payload; }
        public String getSource() { return source; }
        public long getTimestamp() { return timestamp; }
        public boolean isConsumed() { return consumed; }
        public void setConsumed(boolean consumed) { this.consumed = consumed; }
    }
}
