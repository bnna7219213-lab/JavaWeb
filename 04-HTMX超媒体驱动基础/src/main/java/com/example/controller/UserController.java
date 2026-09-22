package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 首页 - 渲染主页面
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("userCount", userService.count());
        return "index";
    }

    /**
     * 获取用户列表片段 (hx-get 调用)
     * 返回 Thymeleaf 渲染的 HTML 片段
     */
    @GetMapping("/api/user/fragment")
    public String getUserFragment(Model model) {
        model.addAttribute("users", userService.findAll());
        return "fragments :: userList";
    }

    /**
     * 获取单个用户信息片段
     */
    @GetMapping("/api/user/{id}/fragment")
    public String getUserById(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresentOrElse(
            user -> model.addAttribute("user", user),
            () -> model.addAttribute("user", new User(0L, "未找到", 0, "N/A"))
        );
        return "fragments :: userDetail";
    }

    /**
     * 创建用户 (hx-post 调用)
     * 返回更新后的用户列表片段
     */
    @PostMapping("/api/user/fragment")
    public String createUser(@RequestParam String name,
                             @RequestParam Integer age,
                             @RequestParam String email,
                             Model model) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);
        userService.save(user);

        model.addAttribute("users", userService.findAll());
        return "fragments :: userList";
    }
}
