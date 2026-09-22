package com.example.astro.controller;

import com.example.astro.entity.Product;
import com.example.astro.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RESTful API 控制器
 *
 * 为所有岛屿组件（Alpine.js / React）提供 JSON 数据接口
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final List<User> users = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();

    public ApiController() {
        users.add(new User(1L, "张三", "zhangsan@example.com", "ZS", "管理员", true, "技术部"));
        users.add(new User(2L, "李四", "lisi@example.com", "LS", "开发者", true, "技术部"));
        users.add(new User(3L, "王五", "wangwu@example.com", "WW", "设计师", false, "设计部"));
        users.add(new User(4L, "赵六", "zhaoliu@example.com", "ZL", "测试工程师", true, "技术部"));
        users.add(new User(5L, "孙七", "sunqi@example.com", "SQ", "产品经理", true, "产品部"));
        users.add(new User(6L, "周八", "zhouba@example.com", "ZB", "运维工程师", false, "技术部"));
        users.add(new User(7L, "吴九", "wujiu@example.com", "WJ", "数据分析师", true, "运营部"));
        users.add(new User(8L, "郑十", "zhengshi@example.com", "Z0", "UI设计师", true, "设计部"));
        users.add(new User(9L, "钱十一", "qian11@example.com", "QY", "前端工程师", true, "技术部"));
        users.add(new User(10L, "何十二", "he12@example.com", "HE", "市场专员", true, "市场部"));

        products.add(new Product(1L, "MacBook Pro 14", "Apple M3 Pro, 18GB, 512GB", new BigDecimal("16999"), "电脑", true, 50, 4.8));
        products.add(new Product(2L, "iPhone 15 Pro", "A17 Pro, 256GB", new BigDecimal("8999"), "手机", true, 200, 4.7));
        products.add(new Product(3L, "AirPods Pro 2", "主动降噪, USB-C", new BigDecimal("1899"), "配件", true, 500, 4.6));
        products.add(new Product(4L, "iPad Air", "M2, 256GB", new BigDecimal("5299"), "平板", false, 0, 4.5));
        products.add(new Product(5L, "Dell U2723QE", "27寸 4K IPS", new BigDecimal("3899"), "显示器", true, 30, 4.4));
        products.add(new Product(6L, "Logitech MX Master 3S", "无线鼠标, 8000dpi", new BigDecimal("699"), "配件", true, 150, 4.9));
        products.add(new Product(7L, "Keychron K2", "75% 机械键盘", new BigDecimal("548"), "配件", true, 80, 4.3));
        products.add(new Product(8L, "Herman Miller Aeron", "人体工学椅", new BigDecimal("9800"), "家具", true, 10, 4.8));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> result = users.stream()
                .filter(u -> u.getName().contains(keyword) || u.getEmail().contains(keyword) || u.getDepartment().contains(keyword))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/by-department")
    public ResponseEntity<Map<String, List<User>>> getUsersByDepartment() {
        Map<String, List<User>> grouped = users.stream()
                .collect(Collectors.groupingBy(User::getDepartment));
        return ResponseEntity.ok(grouped);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/top")
    public ResponseEntity<List<Product>> getTopProducts() {
        List<Product> top = products.stream()
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(5)
                .collect(Collectors.toList());
        return ResponseEntity.ok(top);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updated) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                updated.setId(id);
                products.set(i, updated);
                return ResponseEntity.ok(updated);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", users.size());
        stats.put("activeUsers", users.stream().filter(User::isActive).count());
        stats.put("totalProducts", products.size());
        stats.put("availableProducts", products.stream().filter(Product::isAvailable).count());
        stats.put("totalValue", products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return ResponseEntity.ok(stats);
    }
}
