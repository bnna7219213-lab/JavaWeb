package com.scs.basic.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息记录 - 模拟消息中间件中的消息
 *
 * <p>在真正的SCS架构中，消息通过外部消息中间件（如Kafka、RabbitMQ）传递。
 * 这里使用内存结构模拟消息的持久化存储。</p>
 */
public class MessageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private String messageId;

    /** 消息主题 */
    private String topic;

    /** 消息类型 */
    private String messageType;

    /** 消息内容 (JSON格式) */
    private String payload;

    /** 消息状态 */
    private MessageStatus status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public enum MessageStatus {
        PENDING, PUBLISHED, CONSUMED, FAILED
    }

    public MessageRecord() {
        this.messageId = UUID.randomUUID().toString();
        this.status = MessageStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public MessageRecord(String topic, String messageType, String payload) {
        this();
        this.topic = topic;
        this.messageType = messageType;
        this.payload = payload;
    }

    // ===== Getters & Setters =====

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "messageId='" + messageId + '\'' +
                ", topic='" + topic + '\'' +
                ", messageType='" + messageType + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
