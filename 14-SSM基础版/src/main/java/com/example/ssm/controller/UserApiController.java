package com.example.ssm.controller;

import com.example.ssm.entity.User;
import com.example.ssm.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API 控制器（JSON接口）
 * 路径前缀: /api
 */
@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    private Map<String, Object> ok(String msg, Object data) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", 200);
        r.put("msg", msg);
        r.put("data", data);
        return r;
    }

    private Map<String, Object> err(int code, String msg) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    // ==================== CRUD 接口 ====================

    @GetMapping("/user")
    public Map<String, Object> getById(@RequestParam Integer id) {
        User u = userService.getById(id);
        return u != null ? ok("ok", u) : err(404, "用户不存在");
    }

    @GetMapping("/user/list")
    public Map<String, Object> list(@RequestParam(required = false) String search) {
        List<User> list = (search != null && !search.isBlank())
                ? userService.searchByName(search)
                : userService.listAll();
        return ok("共" + list.size() + "条", list);
    }

    @PostMapping("/user")
    public Map<String, Object> add(@RequestBody User user) {
        boolean ok = userService.addUser(user);
        return ok ? ok("创建成功", user) : err(500, "创建失败");
    }

    @PutMapping("/user")
    public Map<String, Object> update(@RequestBody User user) {
        if (user.getId() == null) return err(400, "缺少ID");
        boolean ok = userService.updateUser(user);
        return ok ? ok("更新成功", user) : err(404, "用户不存在");
    }

    @DeleteMapping("/user")
    public Map<String, Object> delete(@RequestParam Integer id) {
        boolean ok = userService.removeById(id);
        return ok ? ok("删除成功", null) : err(404, "用户不存在");
    }
}
