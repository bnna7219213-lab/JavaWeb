package com.example.controller;

import com.example.service.OrderService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * 首页 - 仪表盘
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userCount", userService.count());
        model.addAttribute("orderCount", orderService.count());
        model.addAttribute("recentUsers", userService.findAll().stream().limit(3).toList());
        model.addAttribute("recentOrders", orderService.findAll().stream().limit(3).toList());
        return "dashboard";
    }
}
