package com.example.hilla.endpoint;

import com.example.hilla.entity.User;
import com.example.hilla.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【模拟 Hilla @Endpoint】用户端点
 *
 * 在真正的 Hilla 框架中，此类会被 @Endpoint 标注，
 * Hilla Maven Plugin 自动扫描并生成 TypeScript 客户端。
 *
 * 对应自动生成的 TypeScript 文件：
 *   frontend/generated/models.d.ts      → User 接口定义
 *   frontend/generated/endpoints.ts      → UserEndpoint 客户端
 */
@RestController
@RequestMapping("/api/user")
public class UserEndpoint {

    private final UserService userService;

    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/user/list
     * 获取所有用户
     */
    @GetMapping("/list")
    public List<User> listUsers() {
        return userService.findAll();
    }

    /**
     * GET /api/user/{id}
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: id=" + id));
    }

    /**
     * POST /api/user
     * 新增用户
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        User saved = userService.save(user);
        System.out.println("[UserEndpoint] 新增用户: " + saved);
        return saved;
    }

    /**
     * PUT /api/user/{id}
     * 更新用户
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updated = userService.update(id, user);
        if (updated == null) {
            throw new RuntimeException("用户不存在: id=" + id);
        }
        System.out.println("[UserEndpoint] 更新用户: " + updated);
        return updated;
    }

    /**
     * DELETE /api/user/{id}
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        boolean deleted = userService.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("删除失败，用户不存在: id=" + id);
        }
        System.out.println("[UserEndpoint] 删除用户 id=" + id);
        return "删除成功";
    }

    /**
     * GET /api/user/count
     * 获取用户总数
     */
    @GetMapping("/count")
    public long countUsers() {
        return userService.count();
    }
}
