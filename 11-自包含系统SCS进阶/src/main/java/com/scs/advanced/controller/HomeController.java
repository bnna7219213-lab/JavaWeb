package com.scs.advanced.controller;

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
        model.addAttribute("scsName", "多SCS协同架构演示");
        model.addAttribute("port", "8100");
        return "index";
    }
}
