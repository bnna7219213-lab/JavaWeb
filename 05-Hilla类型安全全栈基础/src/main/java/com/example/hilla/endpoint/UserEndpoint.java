package com.example.hilla.endpoint;

import com.example.hilla.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 【模拟 Hilla @Endpoint 效果】
 *
 * 在真正的 Hilla 框架中，只需用 @Endpoint 标注一个 Java 类，
 * Hilla 会自动扫描并生成对应的 TypeScript 客户端代码（.d.ts 文件 + endpoints.ts），
 * 前端直接 import 调用即可获得完整类型提示。
 *
 * 本项目使用 @RestController + 模拟方式展示同样的架构效果。
 *
 * 对应自动生成的 TypeScript 客户端文件：
 *   frontend/models.d.ts       - 模型类型定义
 *   src/main/resources/static/frontend/endpoints.ts  - API 客户端
 */
@RestController
@RequestMapping("/api/user")
public class UserEndpoint {

    // 模拟内存数据库
    private static final Map<Integer, User> USER_STORE = new HashMap<>();
    private static int nextId = 1;

    static {
        USER_STORE.put(1, new User(1, "张三", 24, "zhangsan@example.com"));
        USER_STORE.put(2, new User(2, "李四", 30, "lisi@example.com"));
        USER_STORE.put(3, new User(3, "王五", 28, null));
        nextId = 4;
    }

    /**
     * GET /api/user/list
     * 获取所有用户列表
     */
    @GetMapping("/list")
    public List<User> listUsers() {
        return new ArrayList<>(USER_STORE.values());
    }

    /**
     * GET /api/user/{id}
     * 根据 ID 获取单个用户
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        User user = USER_STORE.get(id);
        if (user == null) {
            throw new RuntimeException("用户不存在: id=" + id);
        }
        return user;
    }

    /**
     * POST /api/user/add
     * 新增用户
     */
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        user.setId(nextId++);
        USER_STORE.put(user.getId(), user);
        System.out.println("[UserEndpoint] 新增用户: " + user);
        return user;
    }
}
