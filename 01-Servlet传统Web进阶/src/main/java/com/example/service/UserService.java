package com.example.service;

import com.example.entity.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户业务服务层
 * 模拟数据库存储，实际项目中替换为DAO层
 */
public class UserService {

    // 模拟数据库
    private static final Map<Integer, User> USER_DB = new ConcurrentHashMap<>();
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(100);

    static {
        // 初始化测试数据
        USER_DB.put(1, new User(1, "张三", 24, "zhangsan@example.com"));
        USER_DB.put(2, new User(2, "李四", 30, "lisi@example.com"));
        USER_DB.put(3, new User(3, "王五", 28, "wangwu@example.com"));
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Integer id) {
        return USER_DB.get(id);
    }

    /**
     * 查询所有用户列表
     */
    public List<User> findAll() {
        return new ArrayList<>(USER_DB.values());
    }

    /**
     * 新增用户
     */
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(ID_GENERATOR.incrementAndGet());
        }
        USER_DB.put(user.getId(), user);
        return user;
    }

    /**
     * 删除用户
     */
    public boolean delete(Integer id) {
        return USER_DB.remove(id) != null;
    }

    /**
     * 根据姓名模糊搜索
     */
    public List<User> searchByName(String keyword) {
        List<User> list = new ArrayList<>();
        for (User user : USER_DB.values()) {
            if (user.getName().contains(keyword)) {
                list.add(user);
            }
        }
        return list;
    }
}
