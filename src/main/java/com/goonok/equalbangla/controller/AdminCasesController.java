package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Admin;
import com.goonok.equalbangla.model.Task;
import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.service.AdminService;
import com.goonok.equalbangla.service.TaskService;
import com.goonok.equalbangla.service.VictimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Controller
@RequestMapping("/admin/cases")
public class AdminCasesController {

    @Autowired
    private VictimService victimService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TaskService taskService;  // Assuming you have a TaskService for task management

    @GetMapping
    public String adminCases(Model model, Principal principal) {
        // Get the currently logged-in admin
        Admin currentAdmin = adminService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Admin not found"));

        // Fetch all victims (cases) and tasks assigned to this admin
        List<Victim> victims = victimService.getAllVictims();  // Or you can filter by assigned admin if needed
        List<Task> tasks = taskService.getTasksForAdmin(currentAdmin); // Get tasks assigned to the current admin

        model.addAttribute("victimList", victims);
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentAdmin", currentAdmin);

        model.addAttribute("pageTitle", "All Cases");
        return "admin/case/cases";
    }


    @GetMapping("/{victimId}/assign")
    public String showAssignCasePage(@PathVariable Long victimId, Model model) {
        Victim victim = victimService.getVictimById(victimId);
        List<Admin> admins = adminService.getAllAdmins();
        model.addAttribute("victim", victim);
        model.addAttribute("admins", admins);
        model.addAttribute("pageTitle", "Assign Case");
        return "admin/case/assign";
    }

    @PostMapping("/{victimId}/assign")
    public String assignCaseToAdmin(@PathVariable Long victimId, @RequestParam Long adminId) {
        victimService.assignCaseToAdmin(victimId, adminId);
        return "redirect:/admin/cases";
    }

    @PostMapping("/{victimId}/status")
    public String updateCaseStatus(@PathVariable Long victimId, @RequestParam String status) {
        victimService.updateCaseStatus(victimId, status);
        return "redirect:/admin/cases";
    }

    @PostMapping("/{victimId}/task")
    public String createTask(@PathVariable Long victimId, @RequestParam Long adminId, @RequestParam String description) {
        victimService.createTaskForCase(victimId, adminId, description);
        return "redirect:/admin/cases";
    }
}
