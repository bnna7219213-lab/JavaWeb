package com.example.modulith.advanced.modules.audit.listener;

import com.example.modulith.advanced.modules.audit.service.internal.AuditServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 审计模块控制器 - 展示完整的审计日志
 */
@Controller
@RequestMapping("/audit")
public class AuditController {

    private final AuditServiceImpl auditService;

    public AuditController(AuditServiceImpl auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public String listAuditLogs(Model model) {
        model.addAttribute("auditLogs", auditService.getAuditLogs());
        model.addAttribute("pageTitle", "审计日志 - 模块化单体进阶版");
        return "audit/audit-logs";
    }
}
