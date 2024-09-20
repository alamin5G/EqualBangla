package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Task;
import com.goonok.equalbangla.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/tasks")
public class AdminTaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/{taskId}/complete")
    public String completeTask(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        task.setTaskStatus("COMPLETED");
        taskService.saveTask(task);
        return "redirect:/admin/cases";  // Redirect back to case management page
    }
}
