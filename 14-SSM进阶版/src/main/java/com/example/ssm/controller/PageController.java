package com.example.ssm.controller;

import com.example.ssm.entity.Order;
import com.example.ssm.entity.User;
import com.example.ssm.service.OrderService;
import com.example.ssm.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 页面路由控制器（Thymeleaf HTML）
 */
@Controller
public class PageController {

    private final UserService userService;
    private final OrderService orderService;

    public PageController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("userCount", userService.count(null));
        model.addAttribute("orderCount", orderService.count(null, null));
        return "index";
    }

    @GetMapping("/users.html")
    public String usersPage(Model model,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String keyword) {
        int offset = (page - 1) * size;
        List<User> users = userService.list(keyword);
        long total = userService.count(keyword);
        model.addAttribute("users", users);
        model.addAttribute("total", total);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "users";
    }

    @GetMapping("/orders.html")
    public String ordersPage(Model model,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) Integer userId,
                             @RequestParam(required = false) String status) {
        int offset = (page - 1) * size;
        List<Order> orders = orderService.list(userId, status, offset, size);
        long total = orderService.count(userId, status);
        model.addAttribute("orders", orders);
        model.addAttribute("total", total);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("userId", userId);
        model.addAttribute("status", status);
        return "orders";
    }

}
