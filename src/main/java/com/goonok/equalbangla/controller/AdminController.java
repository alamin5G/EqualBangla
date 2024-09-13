package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.dto.JwtResponse;
import com.goonok.equalbangla.dto.LoginRequest;
import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.security.JwtTokenProvider;
import com.goonok.equalbangla.service.AdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VictimRepository victimRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    // GET request for login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin/login";  // Return the login page view
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpServletResponse response) {
        try {
            // Authenticate using form parameters
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // If successful, generate JWT token
            String token = jwtTokenProvider.createToken(username);

            // Store the token in an HTTP-only cookie for security
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true); // Prevent access via JavaScript
            jwtCookie.setPath("/"); // Make it available for the whole application
            jwtCookie.setMaxAge(7200); // Set the cookie expiry time (2 hours)
            response.addCookie(jwtCookie);

            // Redirect to the dashboard after login
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            // On failure, return back to the login page with an error
            model.addAttribute("error", "Invalid credentials");
            return "admin/login";  // Redirect back to login if failed
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Invalidate the JWT token by clearing the cookie
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);  // Set the JWT token to null
        jwtCookie.setPath("/");  // Make sure it applies to all routes
        jwtCookie.setHttpOnly(true);  // Keep it HTTP-only
        jwtCookie.setMaxAge(0);  // Expire the cookie immediately
        response.addCookie(jwtCookie);

        // Redirect to the login page after logout
        return "redirect:/admin/login";  // You can customize the redirection path
    }

    // GET request for admin dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        return "admin/dashboard";  // Return the dashboard view
    }

    @GetMapping("/verify")
    public String viewPendingVerification(Model model) {
        List<Victim> pendingVictims = victimRepository.findByVerificationStatus("PENDING");
        model.addAttribute("victims", pendingVictims);
        return "admin/verification";
    }

    @PostMapping("/verify")
    public String verifyVictim(@RequestParam Long victimId, @RequestParam String action) {
        Victim victim = victimRepository.findById(victimId).orElseThrow();
        victim.setVerificationStatus(action.equals("approve") ? "VERIFIED" : "REJECTED");
        victimRepository.save(victim);
        return "redirect:/admin/verify";
    }


    @GetMapping("/filter")
    public String filterVictims(@RequestParam String incidentType,
                                @RequestParam(required = false) LocalDate startDate,
                                @RequestParam(required = false) LocalDate endDate,
                                Model model) {

        List<Victim> filteredVictims = victimRepository.findByIncidentTypeAndDateRange(incidentType, startDate, endDate);
        model.addAttribute("victims", filteredVictims);
        return "admin/dashboard"; // The Thymeleaf template name
    }

    @GetMapping("/export/csv")
    public void exportToCSV(@RequestParam String incidentType,
                            @RequestParam(required = false) LocalDate startDate,
                            @RequestParam(required = false) LocalDate endDate,
                            HttpServletResponse response) throws IOException {

        // Set the response type to CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=victims.csv");

        // Retrieve the filtered victims
        List<Victim> victims = victimRepository.findByIncidentTypeAndDateRange(incidentType, startDate, endDate);

        // Write CSV headers
        PrintWriter writer = response.getWriter();
        writer.println("Full Name,Incident Type,Incident Date,Location,Contact Number");

        // Write victim data as CSV
        for (Victim victim : victims) {
            writer.println(victim.getFullName() + "," +
                    victim.getIncidentType() + "," +
                    victim.getIncidentDate() + "," +
                    victim.getIncidentLocation() + "," +
                    victim.getContactNumber());
        }
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(@RequestParam String incidentType,
                            @RequestParam(required = false) LocalDate startDate,
                            @RequestParam(required = false) LocalDate endDate,
                            HttpServletResponse response) throws IOException {

        // Set the response type to PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=victims.pdf");

        // Retrieve the filtered victims
        List<Victim> victims = victimRepository.findByIncidentTypeAndDateRange(incidentType, startDate, endDate);

        // Create a PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a content stream to write to the PDF
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            //contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 700);

            // Write a title
            contentStream.showText("Victim Data Report");
            contentStream.newLine();

            // Write CSV-style data to the PDF
            contentStream.showText("Full Name | Incident Type | Date | Location | Contact Number");
            contentStream.newLine();

            for (Victim victim : victims) {
                contentStream.showText(victim.getFullName() + " | " +
                        victim.getIncidentType() + " | " +
                        victim.getIncidentDate() + " | " +
                        victim.getIncidentLocation() + " | " +
                        victim.getContactNumber());
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.close();

            // Save the document to the response
            document.save(response.getOutputStream());
        }
    }

    @GetMapping("/statistics")
    public String viewStatistics(Model model) {
        //List<Object[]> stats = victimRepository.countByIncidentType();
        //model.addAttribute("statistics", stats);
        return "admin/statistics";
    }



}
