package com.scs.basic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("scsName", "User SCS (自包含用户服务)");
        model.addAttribute("port", "8099");
        return "index";
    }
}
