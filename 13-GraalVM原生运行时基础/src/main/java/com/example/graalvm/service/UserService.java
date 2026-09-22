package com.example.graalvm.service;

import com.example.graalvm.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务层
 *
 * Native Image 注意: Service层类本身不需要特别注册，
 * 但通过其方法序列化/反序列化的实体类需要反射注册。
 */
@Service
public class UserService {

    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // 初始化示例数据（在Native Image中，此逻辑在构建时可能已部分完成）
        initializeSampleData();
    }

    private void initializeSampleData() {
        createUser(new User(null, "alice", "alice@example.com", 28, "ADMIN"));
        createUser(new User(null, "bob", "bob@example.com", 35, "USER"));
        createUser(new User(null, "charlie", "charlie@example.com", 22, "USER"));
    }

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    public User createUser(User user) {
        long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        userStore.put(id, user);
        return user;
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return findByIdId(id).map(existing -> {
            existing.setUsername(updatedUser.getUsername());
            existing.setEmail(updatedUser.getEmail());
            existing.setAge(updatedUser.getAge());
            existing.setRole(updatedUser.getRole());
            return existing;
        });
    }

    public boolean deleteUser(Long id) {
        return userStore.remove(id) != null;
    }

    // 辅助方法（避免IDE警告）
    private Optional<User> findByIdId(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }
}
