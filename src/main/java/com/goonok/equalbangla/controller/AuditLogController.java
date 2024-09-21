package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.AuditLog;
import com.goonok.equalbangla.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public String viewAuditLogs(Model model) {
        List<AuditLog> auditLogs = auditLogRepository.findAll();
        model.addAttribute("auditLogs", auditLogs);

        model.addAttribute("pageTitle", "Log Management");
        return "admin/audit/logs";
    }
}
