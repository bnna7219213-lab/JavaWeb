package com.scs.basic.service;

import com.scs.basic.entity.MessageRecord;
import com.scs.basic.entity.MessageRecord.MessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 消息服务 - 模拟消息中间件的核心功能
 *
 * <p>在真正的SCS架构中，服务间通过消息中间件（如Kafka、RabbitMQ）异步通信。
 * 这里使用内存结构来模拟消息的发布、存储和消费过程，展示SCS异步通信的核心概念。</p>
 *
 * <p>SCS的异步消息通信优势：</p>
 * <ul>
 *   <li>解耦：服务间不需要知道彼此的存在</li>
 *   <li>弹性：消息持久化，消费者故障不丢消息</li>
 *   <li>可扩展：可以任意增加消费者</li>
 * </ul>
 */
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    /**
     * 模拟消息中间件的消息存储
     * 在真正SCS中，这是Kafka Topic或RabbitMQ Queue
     */
    private final Map<String, List<MessageRecord>> topicStorage = new ConcurrentHashMap<>();

    /**
     * 已消费消息记录
     */
    private final List<MessageRecord> consumedMessages = Collections.synchronizedList(new ArrayList<>());

    /**
     * 发布消息到指定Topic
     * 模拟: eventPublisher.publishEvent() -> Kafka Producer
     */
    public MessageRecord publishMessage(String topic, String messageType, String payload) {
        MessageRecord message = new MessageRecord(topic, messageType, payload);
        message.setStatus(MessageStatus.PUBLISHED);

        topicStorage.computeIfAbsent(topic, k -> Collections.synchronizedList(new ArrayList<>()))
                     .add(message);

        log.info("[Message Broker] 消息已发布: topic={}, type={}, msgId={}",
                topic, messageType, message.getMessageId());
        return message;
    }

    /**
     * 消费指定Topic的消息
     * 模拟: @EventListener -> Kafka Consumer
     */
    public List<MessageRecord> consumeMessages(String topic) {
        List<MessageRecord> messages = topicStorage.getOrDefault(topic, Collections.emptyList());
        List<MessageRecord> pendingMessages = messages.stream()
                .filter(m -> m.getStatus() == MessageStatus.PUBLISHED)
                .collect(Collectors.toList());

        // 标记为已消费
        pendingMessages.forEach(m -> {
            m.setStatus(MessageStatus.CONSUMED);
            consumedMessages.add(m);
            log.info("[Message Broker] 消息已消费: topic={}, msgId={}", topic, m.getMessageId());
        });

        return pendingMessages;
    }

    /**
     * 获取Topic中待处理的消息数量
     */
    public int getPendingCount(String topic) {
        return (int) topicStorage.getOrDefault(topic, Collections.emptyList())
                .stream()
                .filter(m -> m.getStatus() == MessageStatus.PUBLISHED)
                .count();
    }

    /**
     * 获取所有Topic的名称
     */
    public Set<String> getAllTopics() {
        return new TreeSet<>(topicStorage.keySet());
    }

    /**
     * 获取中间件整体状态
     */
    public Map<String, Object> getBrokerInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("brokerType", "SCS Internal Message Broker (模拟)");
        info.put("totalTopics", topicStorage.size());
        info.put("totalMessages", topicStorage.values().stream().mapToInt(List::size).sum());
        info.put("totalConsumed", consumedMessages.size());
        info.put("topics", topicStorage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Map.of(
                                "total", e.getValue().size(),
                                "pending", getPendingCount(e.getKey())
                        )
                )));
        return info;
    }

    /**
     * 获取最近的消息(所有Topic)
     */
    public List<MessageRecord> getRecentMessages(int limit) {
        return topicStorage.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(MessageRecord::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
