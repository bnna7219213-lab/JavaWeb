package com.example.controller;

import com.example.entity.Order;
import com.example.entity.User;
import com.example.service.OrderService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 订单管理页面
     */
    @GetMapping("")
    public String orderPage(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("orderCount", orderService.count());
        model.addAttribute("users", userService.findAll());
        return "order/index";
    }

    /**
     * 订单表格片段
     */
    @GetMapping("/table")
    public String orderTable(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "order/fragments :: orderTable";
    }

    /**
     * 订单单行片段
     */
    @GetMapping("/{id}/row")
    public String orderRow(@PathVariable Long id, Model model) {
        orderService.findById(id).ifPresent(order -> model.addAttribute("order", order));
        return "order/fragments :: orderRow";
    }

    /**
     * 新建订单表单
     */
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("users", userService.findAll());
        return "order/fragments :: orderForm";
    }

    /**
     * 保存订单
     */
    @PostMapping("/save")
    public String saveOrder(@RequestParam Long userId,
                            @RequestParam String productName,
                            @RequestParam BigDecimal amount,
                            @RequestParam(defaultValue = "待支付") String status,
                            Model model) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductName(productName);
        order.setAmount(amount);
        order.setStatus(status);
        orderService.save(order);
        model.addAttribute("order", order);
        return "order/fragments :: orderRow";
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               Model model) {
        Order order = orderService.updateStatus(id, status);
        if (order != null) {
            model.addAttribute("order", order);
        }
        return "order/fragments :: orderRow";
    }

    /**
     * 删除订单 - hx-confirm
     */
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id, Model model) {
        boolean deleted = orderService.deleteById(id);
        model.addAttribute("deleted", deleted);
        model.addAttribute("message", deleted ? "订单已删除" : "删除失败");
        return "order/fragments :: empty";
    }

    /**
     * 用户订单筛选
     */
    @GetMapping("/by-user/{userId}")
    public String ordersByUser(@PathVariable Long userId, Model model) {
        model.addAttribute("orders", orderService.findByUserId(userId));
        return "order/fragments :: orderTable";
    }
}
