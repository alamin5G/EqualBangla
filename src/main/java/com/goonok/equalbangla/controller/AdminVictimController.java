package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.service.ReportService;
import com.goonok.equalbangla.service.VictimService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/victims")
public class AdminVictimController {

    @Autowired
    private VictimService victimService;

    @Autowired
    private ReportService reportService;

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
        model.addAttribute("pageTitle", "Victim List");

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
        // Boolean verified = null;
        if ("0".equals(verificationStatus)) {
            verificationStatus = "0";
        } else if ("1".equals(verificationStatus)) {
            verificationStatus = "1";
        } else if ("2".equals(verificationStatus)) {
            verificationStatus = "2";
        } else {
            verificationStatus = "";
        }

        // Call the service to filter victims
        Page<Victim> victimPage = victimService.filterVictims(
                fullName, incidentType, start, end, district, policeStation, ageFrom, ageTo, gender, occupation, verificationStatus, page, size, sortField, sortDir);

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

        model.addAttribute("pageTitle", "Filter Victim List");
        return "admin/victim/list";  // Points to the Thymeleaf template for displaying the victims
    }


    @PostMapping("/{id}/approveOrPending")
    public String approveOrTogglePending(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Victim victim = victimService.getVictimById(id);

        // Toggle between Pending (2) and Approved (1)
        if (victim.getVerificationStatus().equals("1")) {
            victim.setVerificationStatus("2");  // Make it Pending
            redirectAttributes.addFlashAttribute("success", victim.getFullName() + " has been set to in Pending list");
        } else {
            victim.setVerificationStatus("1");  // Approve
            redirectAttributes.addFlashAttribute("success", victim.getFullName() + "'s data has been approved");
        }

        victimService.save(victim);

        return "redirect:/admin/victims";  // Redirect back to victim list
    }

    @PostMapping("/{id}/reject")
    public String rejectVictim(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Victim victim = victimService.getVictimById(id);

        // Set verification status to Rejected (0)
        if (victim.getVerificationStatus().equals("2")) { //if it is in pending state
            victim.setVerificationStatus("0"); //set to reject
            redirectAttributes.addFlashAttribute("success", victim.getFullName() + "'s data has been transfer to rejected list");
        } else if (victim.getVerificationStatus().equals("0")) { // if it is in rejected state
            victim.setVerificationStatus("2"); // set to pending list
            redirectAttributes.addFlashAttribute("success", victim.getFullName() + " has been set to in Pending list");
        }
        victimService.save(victim);
        return "redirect:/admin/victims";  // Redirect back to victim list
    }

    @GetMapping("/export/csv")
    public void exportFilteredVictimsCsv(
            @RequestParam(required = false) String fullName,
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
            HttpServletResponse response) throws IOException {

        // Parse the dates from the request
        LocalDate start = (incidentDateFrom != null && !incidentDateFrom.isEmpty()) ? LocalDate.parse(incidentDateFrom) : null;
        LocalDate end = (incidentDateTo != null && !incidentDateTo.isEmpty()) ? LocalDate.parse(incidentDateTo) : null;

        // Fetch the filtered victims
        List<Victim> victims = victimService.getFilteredVictims(
                fullName, incidentType, start, end, district, policeStation, ageFrom, ageTo, gender, occupation, verificationStatus);

        // Generate and download the CSV
        reportService.generateCsvReport(victims, response);
    }

    @GetMapping("/{id}/verify")
    public String showVerificationPage(@PathVariable Long id, Model model) {
        Victim victim = victimService.getVictimById(id);
        model.addAttribute("victim", victim);
        model.addAttribute("pageTitle", "Verify the Victim");
        return "admin/victim/verify";  // Points to the verification page
    }

    @PostMapping("/{id}/verify")
    public String verifyVictim(
            @PathVariable Long id,
            @RequestParam String verificationStatus,
            @RequestParam String verificationRemarks,
            RedirectAttributes redirectAttributes) {

        victimService.verifyVictim(id, verificationStatus, verificationRemarks);
        redirectAttributes.addFlashAttribute("success", "Victim verification updated successfully.");
        return "redirect:/admin/victims";
    }

    @GetMapping("/search")
    public String searchVictims(@RequestParam String keyword, Model model) {
        List<Victim> searchResults = victimService.searchVictims(keyword);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("pageTitle", "search victim");
        return "admin/victim/search"; // Points to the Thymeleaf template for search results
    }

    @GetMapping("/advanced-search")
    public String advancedSearchVictims(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String incidentType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Convert String to LocalDate
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        // Use pageable for pagination
        Page<Victim> searchResults = victimService.advancedSearch(fullName, incidentType, start, end, region, severity, page, size);

        model.addAttribute("searchResults", searchResults.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("totalItems", searchResults.getTotalElements());

        model.addAttribute("pageTitle", "Search victim");
        // Return the same page or another template where search results will be shown
        return "admin/victim/advanced-search";
    }


}