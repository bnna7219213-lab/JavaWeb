package com.scs.advanced.controller.user;

import com.scs.advanced.entity.user.ScsUser;
import com.scs.advanced.entity.order.ScsOrder;
import com.scs.advanced.service.order.ScsOrderService;
import com.scs.advanced.service.user.ScsUserService;
import com.scs.advanced.service.user.UserScsEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * User SCS 页面控制器
 *
 * <p>路径前缀: /user-scs/</p>
 * <p>这是User SCS自包含的前端UI。</p>
 */
@Controller
@RequestMapping("/user-scs")
public class UserScsPageController {

    @Autowired
    private ScsUserService userService;

    @Autowired
    private UserScsEventPublisher eventPublisher;

    @Autowired
    private ScsOrderService orderService;

    /**
     * User SCS 用户管理首页
     */
    @GetMapping("/")
    public String userHome(Model model) {
        List<ScsUser> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("scsName", "User Self-Contained System");
        model.addAttribute("scsTag", "用户SCS");
        model.addAttribute("userCount", users.size());
        return "user/index";
    }

    /**
     * 创建用户表单页
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("scsName", "User SCS");
        return "user/new";
    }

    /**
     * 处理创建用户
     */
    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) String phone,
                             Model model) {
        try {
            ScsUser user = userService.createUser(username, email, phone);

            // 发布事件 - Order SCS会监听并创建默认订单
            eventPublisher.publishUserCreatedEvent(user);

            model.addAttribute("successMessage",
                    "用户创建成功! 跨SCS事件已发布，Order SCS自动创建了默认订单。");
            return "redirect:/user-scs/";
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
     */
    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable String userId, Model model) {
        Optional<ScsUser> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            String username = userOpt.get().getUsername();
            userService.deleteUser(userId);
            eventPublisher.publishUserDeletedEvent(userId, username);
            model.addAttribute("successMessage",
                    "用户已删除。Order SCS收到UserDeleted事件，已清理关联订单。");
        }
        return "redirect:/user-scs/";
    }

    /**
     * 查看用户关联订单
     */
    @GetMapping("/{userId}/orders")
    public String userOrders(@PathVariable String userId, Model model) {
        Optional<ScsUser> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/user-scs/";
        }

        ScsUser user = userOpt.get();
        List<ScsOrder> orders = orderService.findByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "user/orders";
    }
}
