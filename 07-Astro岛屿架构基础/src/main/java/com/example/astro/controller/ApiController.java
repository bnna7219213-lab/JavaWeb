package com.example.astro.controller;

import com.example.astro.entity.Product;
import com.example.astro.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API Controller - 返回 JSON 数据
 *
 * <p>为"岛屿"组件提供异步数据接口。
 * 在 Astro 中，岛屿可以通过 fetch 获取数据，这里我们模拟这个过程。
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final List<User> users = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();

    public ApiController() {
        users.add(new User(1L, "张三", "zhangsan@example.com", "A", "管理员", true));
        users.add(new User(2L, "李四", "lisi@example.com", "B", "开发者", true));
        users.add(new User(3L, "王五", "wangwu@example.com", "C", "设计师", false));
        users.add(new User(4L, "赵六", "zhaoliu@example.com", "D", "测试工程师", true));
        users.add(new User(5L, "孙七", "sunqi@example.com", "E", "产品经理", true));

        products.add(new Product(1L, "MacBook Pro", "Apple M3 Pro 14英寸", new BigDecimal("16999"), "电脑", true));
        products.add(new Product(2L, "iPhone 15", "A16 芯片 128GB", new BigDecimal("5999"), "手机", true));
        products.add(new Product(3L, "AirPods Pro", "主动降噪蓝牙耳机", new BigDecimal("1899"), "配件", true));
        products.add(new Product(4L, "iPad Air", "M2 芯片 11英寸", new BigDecimal("4799"), "平板", false));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> result = users.stream()
                .filter(u -> u.getName().contains(keyword) || u.getEmail().contains(keyword))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
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

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setId((long) users.size() + 1);
        users.add(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                updatedUser.setId(id);
                users.set(i, updatedUser);
                return ResponseEntity.ok(updatedUser);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
