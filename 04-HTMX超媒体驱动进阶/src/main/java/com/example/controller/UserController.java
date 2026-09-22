package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户管理页面
     */
    @GetMapping("")
    public String userPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("userCount", userService.count());
        return "user/index";
    }

    /**
     * 获取用户列表（表格行片段）- outerHTML swap 用于整表刷新
     */
    @GetMapping("/table")
    public String userTable(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/fragments :: userTable";
    }

    /**
     * 获取用户行（单条）- outerHTML swap 实现行级更新
     */
    @GetMapping("/{id}/row")
    public String userRow(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user/fragments :: userRow";
    }

    /**
     * 获取新建用户表单片段
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user/fragments :: userForm";
    }

    /**
     * 获取编辑用户表单片段
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(new User());
        model.addAttribute("user", user);
        return "user/fragments :: userForm";
    }

    /**
     * 保存用户（创建或更新）- POST 后返回更新后的行
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        userService.save(user);
        model.addAttribute("user", user);
        return "user/fragments :: userRow";
    }

    /**
     * 删除用户 - hx-confirm 确认后 outerHTML 移除行
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        boolean deleted = userService.deleteById(id);
        model.addAttribute("deleted", deleted);
        model.addAttribute("message", deleted ? "用户已删除" : "删除失败，用户不存在");
        // 返回空内容 + outerHTML swap 会让该行从 DOM 中消失
        return "user/fragments :: empty";
    }

    /**
     * 实时搜索 - hx-trigger 防抖，每输入延迟 300ms 后触发
     */
    @GetMapping("/search")
    public String searchUsers(@RequestParam(required = false) String keyword, Model model) {
        if (keyword == null || keyword.isBlank()) {
            model.addAttribute("users", userService.findAll());
        } else {
            model.addAttribute("users", userService.findAll().stream()
                .filter(u -> u.getName().contains(keyword) || u.getEmail().contains(keyword))
                .toList());
        }
        return "user/fragments :: userTable";
    }
}
