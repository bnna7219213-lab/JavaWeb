package com.example.graalvm.controller;

import com.example.graalvm.GraalVmAdvancedApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home page controller - renders performance panels
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "GraalVM Native Image Advanced");
        model.addAttribute("port", 8104);
        model.addAttribute("isNative", GraalVmAdvancedApplication.isNativeImage());
        model.addAttribute("runtimeMode", GraalVmAdvancedApplication.detectRuntimeMode());
        return "index";
    }

    @GetMapping("/compare")
    public String compare(Model model) {
        model.addAttribute("appName", "GraalVM Native Image - Comparison");
        model.addAttribute("port", 8104);
        return "compare";
    }
}
