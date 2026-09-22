package com.example.modulith.advanced.modules.order.controller;

import com.example.modulith.advanced.modules.order.api.OrderModule;
import com.example.modulith.advanced.modules.product.api.ProductModule;
import com.example.modulith.advanced.modules.user.api.UserModule;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("orders", orderModule.findAllOrders());
        model.addAttribute("pageTitle", "订单管理 - 模块化单体进阶版");
        return "order/orders";
    }

    @GetMapping("/create")
    public String createOrderForm(Model model) {
        model.addAttribute("users", userModule.findAllUsers());
        model.addAttribute("products", productModule.findAllProducts());
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
