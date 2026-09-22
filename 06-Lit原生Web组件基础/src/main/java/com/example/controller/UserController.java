package com.example.controller;

import com.example.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final List<User> USERS = new ArrayList<>(Arrays.asList(
            new User(1, "张三", 24, "zhangsan@example.com", "https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan"),
            new User(2, "李四", 30, "lisi@example.com", "https://api.dicebear.com/7.x/avataaars/svg?seed=lisi"),
            new User(3, "王五", 28, "wangwu@example.com", "https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu")
    ));

    /**
     * 获取单个用户
     * GET /api/user?id=1
     */
    @GetMapping("/user")
    public Map<String, Object> getUser(@RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();
        User user = USERS.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (user != null) {
            result.put("code", 200);
            result.put("msg", "查询成功");
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        }
        return result;
    }

    /**
     * 获取所有用户
     * GET /api/user/list
     */
    @GetMapping("/user/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "共" + USERS.size() + "条");
        result.put("data", USERS);
        return result;
    }

    /**
     * 添加用户
     * POST /api/user/add
     */
    @PostMapping("/user/add")
    public Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        int newId = USERS.stream().mapToInt(User::getId).max().orElse(0) + 1;
        user.setId(newId);
        USERS.add(user);
        System.out.println("新增用户：" + user.getName() + "，年龄：" + user.getAge());
        result.put("code", 200);
        result.put("msg", "用户添加成功");
        result.put("data", user);
        return result;
    }
}
