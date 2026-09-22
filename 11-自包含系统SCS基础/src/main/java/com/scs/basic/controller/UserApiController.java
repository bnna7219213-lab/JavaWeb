package com.scs.basic.controller;

import com.scs.basic.entity.User;
import com.scs.basic.entity.User.UserStatus;
import com.scs.basic.service.MessageService;
import com.scs.basic.service.UserEventService;
import com.scs.basic.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户API控制器 - RESTful接口
 *
 * <p>提供完整的用户管理API，路径前缀: /api/v1/users</p>
 *
 * <p>这是SCS对外暴露的API边界，其他SCS通过此API与该服务交互。</p>
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private MessageService messageService;

    /**
     * GET /api/v1/users - 查询所有用户
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listUsers() {
        List<User> users = userService.findAll();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "User SCS");
        response.put("count", users.size());
        response.put("users", users);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/users/{userId} - 查询单个用户
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("scs", "User SCS");
            response.put("user", user.get());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/v1/users - 创建用户
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String phone = request.get("phone");

        if (username == null || email == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "username和email为必填字段"));
        }

        User user = userService.createUser(username, email, phone);

        // 发布事件 - SCS异步通信的核心演示
        userEventService.publishUserCreatedEvent(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "User SCS");
        response.put("message", "用户创建成功");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/users/{userId} - 更新用户
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phone = request.get("phone");

        try {
            User user = userService.updateUser(userId, email, phone);
            userEventService.publishUserUpdatedEvent(user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("scs", "User SCS");
            response.put("message", "用户更新成功");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/users/{userId} - 删除用户
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        String username = user.getUsername();
        userService.deleteUser(userId);
        userEventService.publishUserDeletedEvent(userId, username);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "User SCS");
        response.put("message", "用户删除成功");
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/users/{userId}/status - 变更用户状态
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> changeStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "status字段为必填"));
        }

        try {
            UserStatus newStatus = UserStatus.valueOf(statusStr.toUpperCase());
            User user = userService.changeStatus(userId, newStatus);
            userEventService.publishUserUpdatedEvent(user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("scs", "User SCS");
            response.put("message", "状态更新成功");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "无效的状态值. 可选: ACTIVE, INACTIVE, SUSPENDED"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
