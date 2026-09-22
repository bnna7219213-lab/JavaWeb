package com.example.astro.controller;

import com.example.astro.entity.Product;
import com.example.astro.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 进阶版页面路由
 *
 * 页面：/, /users, /products, /dashboard, /user-analysis
 * 全部使用 Thymeleaf 服务端渲染静态内容，区域内按需水化岛屿组件
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Astro 岛屿架构进阶");
        model.addAttribute("message", "进阶版：多岛屿交互 + React via CDN + 多实体管理");
        return "pages/index";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("pageTitle", "用户管理");
        model.addAttribute("users", getUsers());
        return "pages/users";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("pageTitle", "产品管理");
        model.addAttribute("products", getProducts());
        return "pages/products";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "数据仪表盘");
        model.addAttribute("stats", getDashboardStats());
        return "pages/dashboard";
    }

    @GetMapping("/user-analysis")
    public String userAnalysis(Model model) {
        model.addAttribute("pageTitle", "用户分析");
        model.addAttribute("users", getUsers());
        model.addAttribute("departments", List.of("技术部", "设计部", "产品部", "市场部", "运营部"));
        return "pages/user-analysis";
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
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
        return users;
    }

    private java.util.Map<String, Object> getDashboardStats() {
        java.util.Map<String, Object> stats = new HashMap<>();
        List<User> allUsers = getUsers();
        List<Product> allProducts = getProducts();
        stats.put("totalUsers", allUsers.size());
        stats.put("activeUsers", allUsers.stream().filter(User::isActive).count());
        stats.put("totalProducts", allProducts.size());
        stats.put("availableProducts", allProducts.stream().filter(Product::isAvailable).count());
        stats.put("totalValue", allProducts.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return stats;
    }

    private List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "MacBook Pro 14", "Apple M3 Pro, 18GB, 512GB", new BigDecimal("16999"), "电脑", true, 50, 4.8));
        products.add(new Product(2L, "iPhone 15 Pro", "A17 Pro, 256GB", new BigDecimal("8999"), "手机", true, 200, 4.7));
        products.add(new Product(3L, "AirPods Pro 2", "主动降噪, USB-C", new BigDecimal("1899"), "配件", true, 500, 4.6));
        products.add(new Product(4L, "iPad Air", "M2, 256GB", new BigDecimal("5299"), "平板", false, 0, 4.5));
        products.add(new Product(5L, "Dell U2723QE", "27寸 4K IPS", new BigDecimal("3899"), "显示器", true, 30, 4.4));
        products.add(new Product(6L, "Logitech MX Master 3S", "无线鼠标, 8000dpi", new BigDecimal("699"), "配件", true, 150, 4.9));
        products.add(new Product(7L, "Keychron K2", "75% 机械键盘", new BigDecimal("548"), "配件", true, 80, 4.3));
        products.add(new Product(8L, "Herman Miller Aeron", "人体工学椅", new BigDecimal("9800"), "家具", true, 10, 4.8));
        return products;
    }
}
