package com.goonok.equalbangla.controller;


import com.goonok.equalbangla.service.DatabaseBackupAndRestoreService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/backup")
public class BackupController {

    @Autowired
    private DatabaseBackupAndRestoreService databaseBackupAndRestoreService;

    @GetMapping("/download")
    public void downloadDatabaseBackup(HttpServletResponse response) {
        try {
            //databaseBackupService.backupDatabaseToResponseUsingJdbc(response); //works fine but single line of data
            //databaseBackupService.backupDatabase(response); //entire schema of the workbench
            databaseBackupAndRestoreService.backupDatabaseOnClick(response);
        }catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to download the database backup");
        }
    }


    @GetMapping("/upload")
    public String uploadDatabaseBackup(Model model  ) {

        model.addAttribute("pageTitle", "Upload Database Backup copy");
        return "/admin/database/restore";  // Display the form
    }

    @PostMapping("/upload")
    public String uploadDatabaseBackup(@RequestParam("backupFile") MultipartFile backupFile, RedirectAttributes redirectAttributes) {
        try {
            databaseBackupAndRestoreService.restoreDatabase(backupFile);  // Restore the uploaded backup
            redirectAttributes.addFlashAttribute("success", "Database restored successfully");
            return "redirect:/admin/dashboard?success=true";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
           return "redirect:/admin/dashboard?error=true";
        }
    }
}

