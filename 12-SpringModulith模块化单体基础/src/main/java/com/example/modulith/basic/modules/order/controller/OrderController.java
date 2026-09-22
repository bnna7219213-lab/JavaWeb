package com.example.modulith.basic.modules.order.controller;

import com.example.modulith.basic.modules.order.api.OrderModule;
import com.example.modulith.basic.modules.product.api.ProductModule;
import com.example.modulith.basic.modules.user.api.UserModule;
import com.example.modulith.basic.shared.dto.OrderDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 订单模块控制器
 *
 * 通过接口注入依赖，确保遵循模块化边界。
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderModule orderModule;
    private final UserModule userModule;
    private final ProductModule productModule;

    public OrderController(OrderModule orderModule,
                           UserModule userModule,
                           ProductModule productModule) {
        this.orderModule = orderModule;
        this.userModule = userModule;
        this.productModule = productModule;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderModule.listOrders());
        model.addAttribute("pageTitle", "订单管理 - 模块化单体基础版");
        return "order/orders";
    }

    @GetMapping("/create")
    public String createOrderForm(Model model) {
        model.addAttribute("users", userModule.listUsers());
        model.addAttribute("products", productModule.listProducts());
        return "order/order-create";
    }

    @PostMapping
    public String createOrder(@RequestParam Long userId,
                              @RequestParam Long productId,
                              @RequestParam Integer quantity) {
        orderModule.createOrder(userId, productId, quantity);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/complete")
    public String completeOrder(@PathVariable Long id) {
        orderModule.completeOrder(id);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id) {
        orderModule.cancelOrder(id);
        return "redirect:/orders";
    }
}
