package com.example.hilla.endpoint;

import com.example.hilla.entity.Order;
import com.example.hilla.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【模拟 Hilla @Endpoint】订单端点
 * 提供 User/Order 跨域关联的完整 CRUD
 */
@RestController
@RequestMapping("/api/order")
public class OrderEndpoint {

    private final OrderService orderService;

    @Autowired
    public OrderEndpoint(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /api/order/list
     * 获取所有订单
     */
    @GetMapping("/list")
    public List<Order> listOrders() {
        return orderService.findAll();
    }

    /**
     * GET /api/order/{id}
     * 根据 ID 查询订单
     */
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在: id=" + id));
    }

    /**
     * GET /api/order/user/{userId}
     * 根据用户 ID 查询订单列表
     */
    @GetMapping("/user/{userId}")
    public List<Order> listOrdersByUser(@PathVariable Integer userId) {
        return orderService.findByUserId(userId);
    }

    /**
     * POST /api/order
     * 创建订单
     */
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Order saved = orderService.save(order);
        System.out.println("[OrderEndpoint] 新增订单: " + saved);
        return saved;
    }

    /**
     * PUT /api/order/{id}
     * 更新订单
     */
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Order updated = orderService.update(id, order);
        if (updated == null) {
            throw new RuntimeException("订单不存在: id=" + id);
        }
        System.out.println("[OrderEndpoint] 更新订单: " + updated);
        return updated;
    }

    /**
     * PATCH /api/order/{id}/status
     * 更新订单状态
     */
    @PatchMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在: id=" + id));
        order.setStatus(body.get("status"));
        Order updated = orderService.update(id, order);
        System.out.println("[OrderEndpoint] 更新订单状态: id=" + id + ", status=" + body.get("status"));
        return updated;
    }

    /**
     * DELETE /api/order/{id}
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("删除失败，订单不存在: id=" + id);
        }
        System.out.println("[OrderEndpoint] 删除订单 id=" + id);
        return "删除成功";
    }
}
