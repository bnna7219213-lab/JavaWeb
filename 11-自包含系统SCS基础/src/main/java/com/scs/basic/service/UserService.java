package com.scs.basic.service;

import com.scs.basic.entity.User;
import com.scs.basic.entity.User.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户业务服务
 *
 * <p>这是SCS内部的核心业务服务，管理用户的完整生命周期。
 * 在真正SCS中，该服务拥有自己的数据库，不与其他SCS共享数据存储。</p>
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 模拟SCS自有数据库 - 用户表
     * 在真正SCS中，每个服务有自己的独立数据库实例
     */
    private final Map<String, User> userDatabase = new ConcurrentHashMap<>();

    /**
     * 创建用户
     */
    public User createUser(String username, String email, String phone) {
        // 业务校验
        Objects.requireNonNull(username, "用户名不能为空");
        Objects.requireNonNull(email, "邮箱不能为空");

        // 检查唯一性
        boolean emailExists = userDatabase.values().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            throw new IllegalArgumentException("邮箱已被注册: " + email);
        }

        boolean usernameExists = userDatabase.values().stream()
                .anyMatch(u -> username.equalsIgnoreCase(u.getUsername()));
        if (usernameExists) {
            throw new IllegalArgumentException("用户名已存在: " + username);
        }

        User user = new User(username, email, phone);
        userDatabase.put(user.getUserId(), user);

        log.info("[User SCS] 用户创建成功: userId={}, username={}", user.getUserId(), username);
        return user;
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(userDatabase.get(userId));
    }

    /**
     * 根据用户名查询
     */
    public Optional<User> findByUsername(String username) {
        return userDatabase.values().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return userDatabase.values().stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     */
    public User updateUser(String userId, String email, String phone) {
        User user = userDatabase.get(userId);
        if (user == null) {
            throw new NoSuchElementException("用户不存在: " + userId);
        }

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        if (phone != null && !phone.isBlank()) {
            user.setPhone(phone);
        }

        log.info("[User SCS] 用户更新成功: userId={}", userId);
        return user;
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(String userId) {
        User removed = userDatabase.remove(userId);
        if (removed != null) {
            log.info("[User SCS] 用户删除成功: userId={}, username={}", userId, removed.getUsername());
            return true;
        }
        return false;
    }

    /**
     * 变更用户状态
     */
    public User changeStatus(String userId, UserStatus newStatus) {
        User user = userDatabase.get(userId);
        if (user == null) {
            throw new NoSuchElementException("用户不存在: " + userId);
        }
        user.setStatus(newStatus);
        log.info("[User SCS] 用户状态变更: userId={}, status={}", userId, newStatus);
        return user;
    }

    /**
     * 获取用户总数
     */
    public long count() {
        return userDatabase.size();
    }

    /**
     * 获取数据库状态信息 (SCS自包含特性演示)
     */
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("databaseName", "USER_SCS_DB (内存模拟)");
        info.put("totalRecords", userDatabase.size());
        info.put("storageEngine", "ConcurrentHashMap");
        info.put("isolationLevel", "完全隔离 - 仅User SCS可访问");
        info.put("scsUnit", "User Self-Contained System");
        return info;
    }
}
