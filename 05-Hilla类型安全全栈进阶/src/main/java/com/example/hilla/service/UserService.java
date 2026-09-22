package com.example.hilla.service;

import com.example.hilla.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户服务层（内存实现，模拟数据库操作）
 */
@Service
public class UserService {

    private final Map<Integer, User> userStore = new HashMap<>();

    public UserService() {
        // 初始化测试数据
        User u1 = new User(1, "张三", 24, "zhangsan@example.com");
        User u2 = new User(2, "李四", 30, "lisi@example.com");
        User u3 = new User(3, "王五", 28, null);
        userStore.put(1, u1);
        userStore.put(2, u2);
        userStore.put(3, u3);
    }

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(userStore.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            int maxId = userStore.keySet().stream().max(Integer::compareTo).orElse(0);
            user.setId(maxId + 1);
        }
        userStore.put(user.getId(), user);
        return user;
    }

    public User update(Integer id, User user) {
        if (!userStore.containsKey(id)) {
            return null;
        }
        user.setId(id);
        userStore.put(id, user);
        return user;
    }

    public boolean deleteById(Integer id) {
        return userStore.remove(id) != null;
    }

    public long count() {
        return userStore.size();
    }
}
