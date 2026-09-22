package com.scs.advanced.controller.user;

import com.scs.advanced.entity.user.ScsUser;
import com.scs.advanced.entity.user.ScsUser.UserStatus;
import com.scs.advanced.event.UserCreatedEvent;
import com.scs.advanced.event.UserDeletedEvent;
import com.scs.advanced.service.user.ScsUserService;
import com.scs.advanced.service.user.UserScsEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * User SCS API控制器
 *
 * <p>路径前缀: /user-scs/api/v1/users</p>
 * <p>这是User SCS对外暴露的API边界。</p>
 */
@RestController
@RequestMapping("/user-scs/api/v1/users")
public class UserScsApiController {

    private static final Logger log = LoggerFactory.getLogger(UserScsApiController.class);

    @Autowired
    private ScsUserService userService;

    @Autowired
    private UserScsEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listUsers() {
        List<ScsUser> users = userService.findAll();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "User SCS");
        response.put("count", users.size());
        response.put("users", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        Optional<ScsUser> user = userService.findById(userId);
        if (user.isPresent()) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("scs", "User SCS");
            response.put("user", user.get());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

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

        try {
            ScsUser user = userService.createUser(username, email, phone);

            // 发布UserCreated事件 -> Order SCS监听到后会创建默认订单
            eventPublisher.publishUserCreatedEvent(user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("scs", "User SCS");
            response.put("message", "用户创建成功 (跨SCS事件已发布，Order SCS将自动创建默认订单)");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        try {
            ScsUser user = userService.updateUser(userId, request.get("email"), request.get("phone"));
            return ResponseEntity.ok(Map.of("scs", "User SCS", "user", (Object) user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Optional<ScsUser> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ScsUser user = userOpt.get();
        userService.deleteUser(userId);

        // 发布UserDeleted事件 -> Order SCS监听到后会清理关联订单
        eventPublisher.publishUserDeletedEvent(userId, user.getUsername());

        return ResponseEntity.ok(Map.of(
                "scs", "User SCS",
                "message", "用户已删除 (跨SCS事件已发布，Order SCS将清理关联订单)",
                "userId", userId
        ));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> changeStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        try {
            UserStatus status = UserStatus.valueOf(request.get("status").toUpperCase());
            ScsUser user = userService.changeStatus(userId, status);
            return ResponseEntity.ok(Map.of("scs", "User SCS", "user", (Object) user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
