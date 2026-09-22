package com.example.modulith.advanced.modules.user.controller;

import com.example.modulith.advanced.modules.user.api.UserModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserModule userModule;

    public UserController(UserModule userModule) {
        this.userModule = userModule;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userModule.findAllUsers());
        model.addAttribute("pageTitle", "用户管理 - 模块化单体进阶版");
        return "user/users";
    }

    @PostMapping
    public String createUser(@RequestParam String name, @RequestParam String email) {
        userModule.createUser(name, email);
        return "redirect:/users";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        userModule.deactivateUser(id);
        return "redirect:/users";
    }
}
