package com.example.modulith.basic.modules.user.controller;

import com.example.modulith.basic.modules.user.api.UserModule;
import com.example.modulith.basic.shared.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块控制器
 *
 * 仅作为前端展示用途，演示模块化架构中的Controller层。
 * 跨模块调用必须通过UserModule接口，不允许直接访问UserService。
 */
@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserModule userModule;

    /**
     * 通过UserModule接口注入，遵循依赖倒置原则
     */
    public UserController(UserModule userModule) {
        this.userModule = userModule;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userModule.listUsers());
        model.addAttribute("pageTitle", "用户管理 - 模块化单体基础版");
        return "user/users";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        UserDTO user = userModule.getUser(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "user/user-detail";
    }

    @PostMapping
    public String createUser(@RequestParam String name, @RequestParam String email) {
        UserDTO userDTO = UserDTO.builder()
                .name(name)
                .email(email)
                .build();
        userModule.createUser(userDTO);
        return "redirect:/users";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        userModule.deactivateUser(id);
        return "redirect:/users";
    }
}
