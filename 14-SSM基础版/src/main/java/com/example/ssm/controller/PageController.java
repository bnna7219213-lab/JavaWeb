package com.example.ssm.controller;

import com.example.ssm.entity.User;
import com.example.ssm.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 页面路由控制器（Thymeleaf HTML，非JSP）
 * 负责渲染服务端HTML
 */
@Controller
public class PageController {

    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 首页 - 展示用户列表（服务端渲染）
     */
    @GetMapping({"/", "/index.html"})
    public String index(Model model,
                        @RequestParam(required = false) String search) {
        List<User> users;
        if (search != null && !search.isBlank()) {
            users = userService.searchByName(search);
        } else {
            users = userService.listAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "index";
    }

    /**
     * 用户详情页
     */
    @GetMapping("/user/detail")
    public String detail(@RequestParam Integer id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "detail";
    }

}
