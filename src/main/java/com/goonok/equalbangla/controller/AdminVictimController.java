package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.service.VictimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/victims")
public class AdminVictimController {

    @Autowired
    private VictimService victimService;

    @GetMapping
    public String listVictims(
            @RequestParam(required = false) String status,  // for filtering by status
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "incidentDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Page<Victim> victimPage = victimService.getVictimsByStatus(status, page, size, sortField, sortDir);

        model.addAttribute("victimPage", victimPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", victimPage.getTotalPages());
        model.addAttribute("totalItems", victimPage.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        // Render the selected status back to the UI
        model.addAttribute("status", status);

        return "admin/victim/victims";
    }


    @GetMapping("/filter")
    public String filterVictims(
            @RequestParam(required = false) String fullName,  // New Name Filter
            @RequestParam(required = false) List<String> incidentType,
            @RequestParam(required = false) String incidentDateFrom,
            @RequestParam(required = false) String incidentDateTo,
            @RequestParam(required = false) List<String> district,
            @RequestParam(required = false) String policeStation,
            @RequestParam(required = false) Integer ageFrom,
            @RequestParam(required = false) Integer ageTo,
            @RequestParam(required = false) List<String> gender,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "incidentDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // Process date filtering
        LocalDate start = (incidentDateFrom != null && !incidentDateFrom.isEmpty()) ? LocalDate.parse(incidentDateFrom) : null;
        LocalDate end = (incidentDateTo != null && !incidentDateTo.isEmpty()) ? LocalDate.parse(incidentDateTo) : null;

        // Process verification status
        Boolean verified = null;
        if ("Verified".equals(verificationStatus)) {
            verified = true;
        } else if ("Unverified".equals(verificationStatus)) {
            verified = false;
        }

        // Call the service to filter victims
        Page<Victim> victimPage = victimService.filterVictims(
                fullName, incidentType, start, end, district, policeStation, ageFrom, ageTo, gender, occupation, verified, page, size, sortField, sortDir);

        model.addAttribute("victimPage", victimPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", victimPage.getTotalPages());
        model.addAttribute("totalItems", victimPage.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        // For rendering back filters
        model.addAttribute("fullName", fullName);  // Add fullName to the model
        model.addAttribute("incidentType", incidentType);
        model.addAttribute("incidentDateFrom", incidentDateFrom);
        model.addAttribute("incidentDateTo", incidentDateTo);
        model.addAttribute("district", district);
        model.addAttribute("policeStation", policeStation);
        model.addAttribute("ageFrom", ageFrom);
        model.addAttribute("ageTo", ageTo);
        model.addAttribute("gender", gender);
        model.addAttribute("occupation", occupation);
        // Add verificationStatus to model to render it in the view
        model.addAttribute("verificationStatus", verificationStatus);

        return "admin/victim/list";  // Points to the Thymeleaf template for displaying the victims
    }
    // Method to update the verification status
    @PostMapping("/{victimId}/update-status")
    public String updateVerificationStatus(
            @PathVariable Long victimId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {

        victimService.updateVerificationStatus(victimId, status);
        redirectAttributes.addFlashAttribute("success", "Verification status updated successfully!");

        return "redirect:/admin/victims";
    }
}