package com.example.modulith.advanced.modules.notification.listener;

import com.example.modulith.advanced.modules.notification.service.internal.NotificationServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 通知模块控制器 - 展示事件驱动的通知记录
 */
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    public NotificationController(NotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listNotifications(Model model) {
        model.addAttribute("notifications", notificationService.getNotifications());
        model.addAttribute("pageTitle", "通知中心 - 模块化单体进阶版");
        return "notification/notifications";
    }
}
