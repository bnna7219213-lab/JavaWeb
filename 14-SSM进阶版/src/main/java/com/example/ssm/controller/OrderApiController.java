package com.example.ssm.controller;

import com.example.ssm.common.PageQuery;
import com.example.ssm.common.Result;
import com.example.ssm.entity.Order;
import com.example.ssm.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单 REST API 控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<Order> getById(@RequestParam Integer id) {
        Order o = orderService.getById(id);
        return o != null ? Result.ok("ok", o) : Result.err(404, "订单不存在");
    }

    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam(required = false) Integer userId,
                                     @RequestParam(required = false) String status,
                                     @ModelAttribute PageQuery pq) {
        List<Order> list = orderService.list(userId, status, pq.getOffset(), pq.getSize());
        long total = orderService.count(userId, status);
        return Result.okPage("共" + total + "条", list, total, pq.getPage(), pq.getSize());
    }

    @PostMapping
    public Result<Order> create(@RequestBody Order order) {
        orderService.createOrder(order);
        return Result.ok("创建成功", order);
    }

    @PatchMapping("/status")
    public Result<Void> updateStatus(@RequestParam Integer id, @RequestParam String status) {
        boolean ok = orderService.updateStatus(id, status);
        return ok ? Result.ok("状态更新成功", null) : Result.err(404, "订单不存在");
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam Integer id) {
        boolean ok = orderService.removeById(id);
        return ok ? Result.ok("删除成功", null) : Result.err(404, "订单不存在");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.ok("ok", orderService.stats());
    }
}
