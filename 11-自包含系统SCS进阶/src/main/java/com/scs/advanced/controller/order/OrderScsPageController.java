package com.scs.advanced.controller.order;

import com.scs.advanced.entity.order.ScsOrder;
import com.scs.advanced.entity.user.ScsUser;
import com.scs.advanced.service.order.ScsOrderService;
import com.scs.advanced.service.user.ScsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Order SCS 页面控制器
 *
 * <p>路径前缀: /order-scs/</p>
 * <p>这是Order SCS自包含的前端UI。</p>
 */
@Controller
@RequestMapping("/order-scs")
public class OrderScsPageController {

    @Autowired
    private ScsOrderService orderService;

    @Autowired
    private ScsUserService userService;

    /**
     * Order SCS 订单管理首页
     */
    @GetMapping("/")
    public String orderHome(Model model) {
        List<ScsOrder> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("scsName", "Order Self-Contained System");
        model.addAttribute("scsTag", "订单SCS");
        model.addAttribute("orderCount", orders.size());

        // 获取所有用户信息用于创建订单时选择
        model.addAttribute("users", userService.findAll());
        return "order/index";
    }

    /**
     * 创建订单页
     */
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("scsName", "Order SCS");
        model.addAttribute("users", userService.findAll());
        return "order/new";
    }

    /**
     * 处理创建订单
     */
    @PostMapping("/create")
    public String createOrder(@RequestParam String userId,
                              @RequestParam String description,
                              Model model) {
        try {
            String username = userService.findById(userId)
                    .map(ScsUser::getUsername)
                    .orElse("Unknown User");

            orderService.createOrder(userId, username, description);
            model.addAttribute("successMessage", "订单创建成功!");
            return "redirect:/order-scs/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("users", userService.findAll());
            return "order/new";
        }
    }

    /**
     * 查看订单详情
     */
    @GetMapping("/{orderId}")
    public String orderDetail(@PathVariable String orderId, Model model) {
        Optional<ScsOrder> orderOpt = orderService.findById(orderId);
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
            model.addAttribute("scsName", "Order SCS");
            return "order/detail";
        }
        return "redirect:/order-scs/";
    }
}
