package com.example.service;

import com.example.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // 初始化示例数据
        users.add(new User(idGenerator.getAndIncrement(), "张三", 28, "zhangsan@example.com"));
        users.add(new User(idGenerator.getAndIncrement(), "李四", 32, "lisi@example.com"));
        users.add(new User(idGenerator.getAndIncrement(), "王五", 25, "wangwu@example.com"));
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public Optional<User> findById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public User save(User user) {
        user.setId(idGenerator.getAndIncrement());
        users.add(user);
        return user;
    }

    public boolean deleteById(Long id) {
        return users.removeIf(u -> u.getId().equals(id));
    }

    public int count() {
        return users.size();
    }
}
