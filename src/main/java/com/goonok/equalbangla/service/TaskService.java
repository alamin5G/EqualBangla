package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.Admin;
import com.goonok.equalbangla.model.Task;
import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private VictimService victimService;

    public List<Task> getTasksForAdmin(Admin admin) {
        return taskRepository.findByAssignedAdmin(admin);
    }

    // Create a new task assigned to an admin for a specific victim
    public Task createTask(Long victimId, Long adminId, String description) {
        // Fetch the Victim using the provided victimId
        Victim victim = victimService.getVictimById(victimId);

        // Fetch the Admin using the provided adminId
        Admin admin = adminService.getAdminById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));

        // Create a new Task
        Task task = new Task();
        task.setVictim(victim);
        task.setAssignedAdmin(admin);
        task.setDescription(description);
        task.setTaskStatus("PENDING");  // Initial status of the task is pending

        // Save the task to the database
        return taskRepository.save(task);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).get();
    }

    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public void saveTask(Task task) {
        taskRepository.save(task);
    }
}
