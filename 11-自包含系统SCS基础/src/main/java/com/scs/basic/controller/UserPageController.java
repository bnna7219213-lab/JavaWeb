package com.scs.basic.controller;

import com.scs.basic.entity.User;
import com.scs.basic.entity.User.UserStatus;
import com.scs.basic.service.MessageService;
import com.scs.basic.service.UserEventService;
import com.scs.basic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户页面控制器 - SCS自包含的前端UI
 *
 * <p>这是SCS自包含特性的核心体现：每个SCS自带UI。</p>
 * <p>路径前缀: /user/</p>
 *
 * <p>在真正SCS中，这意味着不同的SCS部署在不同的子域名:</p>
 * <ul>
 *   <li>users.example.com -> User SCS前端</li>
 *   <li>orders.example.com -> Order SCS前端</li>
 * </ul>
 *
 * <p>这里为了演示，放在同一个jar的不同路径下。</p>
 */
@Controller
@RequestMapping("/user")
public class UserPageController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private MessageService messageService;

    /**
     * 用户管理首页
     * GET /user/
     */
    @GetMapping("/")
    public String userPage(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("scsName", "User SCS (自包含用户服务)");
        model.addAttribute("scsDescription", "这是一个完整的自包含系统单元 - 拥有独立的前端UI、后端API、数据存储和消息系统");
        model.addAttribute("userCount", users.size());
        return "user/index";
    }

    /**
     * 创建用户表单页
     * GET /user/new
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("scsName", "User SCS");
        return "user/new";
    }

    /**
     * 处理创建用户请求
     * POST /user/create
     */
    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) String phone,
                             Model model) {
        try {
            User user = userService.createUser(username, email, phone);
            // 发布事件
            userEventService.publishUserCreatedEvent(user);

            model.addAttribute("successMessage", "用户创建成功: " + user.getUsername());
            return "redirect:/user/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            return "user/new";
        }
    }

    /**
     * 删除用户
     * POST /user/delete/{userId}
     */
    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable String userId, Model model) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            String username = userOpt.get().getUsername();
            userService.deleteUser(userId);
            userEventService.publishUserDeletedEvent(userId, username);
            model.addAttribute("successMessage", "用户已删除");
        }
        return "redirect:/user/";
    }

    /**
     * 消息监控页面 - 展示SCS的消息系统
     * GET /user/messages
     */
    @GetMapping("/messages")
    public String messageMonitor(Model model) {
        model.addAttribute("scsName", "User SCS - Message Monitor");
        model.addAttribute("topics", messageService.getAllTopics());
        model.addAttribute("brokerInfo", messageService.getBrokerInfo());
        model.addAttribute("recentMessages", messageService.getRecentMessages(20));
        return "user/messages";
    }

    /**
     * SCS系统信息页面
     * GET /user/scs-info
     */
    @GetMapping("/scs-info")
    public String scsInfo(Model model) {
        model.addAttribute("scsName", "User Self-Contained System");
        model.addAttribute("scsInfo", Map.of(
                "name", "User SCS",
                "version", "1.0.0",
                "port", "8099",
                "database", "USER_SCS_DB (内存模拟)",
                "messageBroker", "SCS Internal Message Broker",
                "frontend", "Thymeleaf Templates (同jar内)",
                "apiPrefix", "/api/v1/users",
                "pagePrefix", "/user/"
        ));
        model.addAttribute("dbInfo", userService.getDatabaseInfo());
        return "user/scs-info";
    }
}
