package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private Map<String, Object> ok(String msg, Object data) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", 200); r.put("msg", msg); r.put("data", data);
        return r;
    }

    private Map<String, Object> err(int code, String msg) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", code); r.put("msg", msg);
        return r;
    }

    // ========== 单资源CRUD ==========

    @GetMapping("/user")
    public Map<String, Object> getUser(@RequestParam Integer id) {
        User u = userService.findById(id);
        return u != null ? ok("查询成功", u) : err(404, "用户不存在");
    }

    @PostMapping("/user")
    public Map<String, Object> addUser(@RequestBody User user) {
        User saved = userService.save(user);
        return ok("创建成功", saved);
    }

    @PutMapping("/user")
    public Map<String, Object> updateUser(@RequestBody User user) {
        if (user.getId() == null) return err(400, "缺少ID");
        User saved = userService.save(user);
        return ok("更新成功", saved);
    }

    @DeleteMapping("/user")
    public Map<String, Object> deleteUser(@RequestParam Integer id) {
        boolean ok = userService.delete(id);
        return ok ? ok("删除成功", null) : err(404, "用户不存在");
    }

    // ========== 集合资源 ==========

    @GetMapping("/user/list")
    public Map<String, Object> list(@RequestParam(required = false) String search) {
        List<User> list = (search != null && !search.isEmpty()) ? userService.search(search) : userService.findAll();
        return ok("共" + list.size() + "条", list);
    }
}
