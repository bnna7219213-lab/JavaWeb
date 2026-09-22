package com.example.service;

import com.example.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private static final Map<Integer, User> DB = new ConcurrentHashMap<>();
    private static final AtomicInteger GEN = new AtomicInteger(100);

    static {
        DB.put(1, new User(1, "张三", 24, "zhangsan@example.com", "13800001111"));
        DB.put(2, new User(2, "李四", 30, "lisi@example.com", "13800002222"));
        DB.put(3, new User(3, "王五", 28, "wangwu@example.com", "13800003333"));
    }

    public User findById(Integer id) { return DB.get(id); }

    public List<User> findAll() { return new ArrayList<>(DB.values()); }

    public User save(User user) {
        if (user.getId() == null) user.setId(GEN.incrementAndGet());
        DB.put(user.getId(), user);
        return user;
    }

    public boolean delete(Integer id) { return DB.remove(id) != null; }

    public List<User> search(String keyword) {
        List<User> list = new ArrayList<>();
        for (User u : DB.values()) {
            if (u.getName().contains(keyword) || (u.getEmail() != null && u.getEmail().contains(keyword))) {
                list.add(u);
            }
        }
        return list;
    }
}
