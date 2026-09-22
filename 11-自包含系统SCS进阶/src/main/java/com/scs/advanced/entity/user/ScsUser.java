package com.scs.advanced.entity.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SCS用户实体 - User SCS的领域模型
 *
 * <p>注意：这是User SCS自己定义的领域模型。
 * 其他SCS（如Order SCS）有自己的User视图（如 ScsOrderUser），
 * 不同SCS之间的模型是完全独立的。</p>
 */
public class ScsUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String email;
    private String phone;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    public ScsUser() {
        this.userId = "USR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ScsUser(String username, String email, String phone) {
        this();
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    // ===== Getters & Setters =====

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ScsUser{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
