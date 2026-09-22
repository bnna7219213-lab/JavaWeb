package com.example.ssm.controller;

import com.example.ssm.common.Result;
import com.example.ssm.entity.User;
import com.example.ssm.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 REST API 控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<User> getById(@RequestParam Integer id) {
        User u = userService.getById(id);
        return u != null ? Result.ok("ok", u) : Result.err(404, "用户不存在");
    }

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String keyword) {
        List<User> list = userService.list(keyword);
        return Result.okPage("共" + list.size() + "条", list, userService.count(keyword), 1, list.size());
    }

    @PostMapping
    public Result<User> add(@RequestBody User user) {
        userService.addUser(user);
        return Result.ok("创建成功", user);
    }

    @PutMapping
    public Result<User> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.ok("更新成功", user);
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam Integer id) {
        boolean ok = userService.removeById(id);
        return ok ? Result.ok("删除成功", null) : Result.err(404, "用户不存在");
    }
}
