package com.example.astro.controller;

import com.example.astro.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面路由 Controller
 *
 * <p>返回 Thymeleaf 模板渲染后的 HTML 页面，模拟 Astro 的静态生成。
 * 默认全部内容都是服务端渲染的静态 HTML，首屏不需要任何 JS 即可展示。
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Astro岛屿架构基础");
        model.addAttribute("message", "这是服务端渲染的静态内容，首屏零 JS。");
        return "pages/index";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("pageTitle", "用户管理");
        model.addAttribute("users", getSampleUsers());
        return "pages/users";
    }

    private List<User> getSampleUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "张三", "zhangsan@example.com", "A", "管理员", true));
        users.add(new User(2L, "李四", "lisi@example.com", "B", "开发者", true));
        users.add(new User(3L, "王五", "wangwu@example.com", "C", "设计师", false));
        users.add(new User(4L, "赵六", "zhaoliu@example.com", "D", "测试工程师", true));
        users.add(new User(5L, "孙七", "sunqi@example.com", "E", "产品经理", true));
        users.add(new User(6L, "周八", "zhouba@example.com", "F", "运维工程师", false));
        users.add(new User(7L, "吴九", "wujiu@example.com", "G", "数据分析师", true));
        users.add(new User(8L, "郑十", "zhengshi@example.com", "H", "UI设计师", true));
        return users;
    }
}
