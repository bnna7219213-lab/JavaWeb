package com.scs.advanced.controller.order;

import com.scs.advanced.entity.order.ScsOrder;
import com.scs.advanced.service.order.ScsOrderService;
import com.scs.advanced.service.user.ScsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Order SCS API控制器
 *
 * <p>路径前缀: /order-scs/api/v1/orders</p>
 */
@RestController
@RequestMapping("/order-scs/api/v1/orders")
public class OrderScsApiController {

    @Autowired
    private ScsOrderService orderService;

    @Autowired
    private ScsUserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listOrders() {
        List<ScsOrder> orders = orderService.findAll();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "Order SCS");
        response.put("count", orders.size());
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String orderId) {
        Optional<ScsOrder> order = orderService.findById(orderId);
        if (order.isPresent()) {
            return ResponseEntity.ok(Map.of("scs", "Order SCS", "order", (Object) order.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUser(@PathVariable String userId) {
        List<ScsOrder> orders = orderService.findByUserId(userId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "Order SCS");
        response.put("userId", userId);
        response.put("count", orders.size());
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String username = request.get("username");
        String description = request.get("description");

        if (userId == null || description == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "userId和description为必填字段"));
        }

        // 验证用户存在 (通过事件获取username，如果未提供)
        if (username == null) {
            username = userService.findById(userId)
                    .map(com.scs.advanced.entity.user.ScsUser::getUsername)
                    .orElse("Unknown User");
        }

        ScsOrder order = orderService.createOrder(userId, username, description);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scs", "Order SCS");
        response.put("message", "订单创建成功");
        response.put("order", order);
        return ResponseEntity.ok(response);
    }
}
