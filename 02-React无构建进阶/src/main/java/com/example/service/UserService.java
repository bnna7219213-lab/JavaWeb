package com.example.service;

import com.example.entity.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserService {
    private static final Map<Integer, User> DB = new ConcurrentHashMap<>();
    private static final AtomicInteger GEN = new AtomicInteger(100);

    static {
        DB.put(1, new User(1, "张三", 24, "zs@example.com"));
        DB.put(2, new User(2, "李四", 30, "ls@example.com"));
        DB.put(3, new User(3, "王五", 28, "ww@example.com"));
    }

    public User findById(Integer id) { return DB.get(id); }
    public List<User> findAll() { return new ArrayList<>(DB.values()); }
    public User save(User u) {
        if (u.getId() == null) u.setId(GEN.incrementAndGet());
        DB.put(u.getId(), u);
        return u;
    }
    public boolean delete(Integer id) { return DB.remove(id) != null; }
    public List<User> search(String key) {
        List<User> list = new ArrayList<>();
        for (User u : DB.values()) {
            if (u.getName() != null && u.getName().contains(key)) list.add(u);
        }
        return list;
    }
}
