package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Admin;
import com.goonok.equalbangla.power_admin.CustomAdminDetails;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.security.JwtTokenProvider;
import com.goonok.equalbangla.service.AdminService;
import com.goonok.equalbangla.service.GreetingService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VictimRepository victimRepository;

    @Autowired
    private GreetingService greetingService;

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
    public String login(@RequestParam String username, @RequestParam String password, Model model,
                        HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
            jwtCookie.setMaxAge(3600); // Set the cookie expiry time (1 hour)
            response.addCookie(jwtCookie);
            redirectAttributes.addFlashAttribute("success", greetingService.greet(LocalTime.now()) + ", " + username + "!" );
            // Redirect to the dashboard after login
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            // On failure, return back to the login page with an error
            model.addAttribute("error", "Invalid credentials");
            return "admin/login";  // Redirect back to login if failed
        }
    }

    // Registration form (GET request)
    @GetMapping("/register")
    public String showRegistrationForm(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        // Extract token from the cookie or header
        String token = jwtTokenProvider.resolveToken(request);

        // Validate token
        if (token != null && jwtTokenProvider.validateToken(token)) {
            model.addAttribute("admin", new Admin());
            return "admin/register";  // Show registration page
        } else {
            log.info("Token is null: " + token);
            redirectAttributes.addFlashAttribute("error", "Your session has been expired");
            return "redirect:/admin/login";  // Redirect to login if not authenticated
        }
    }

    // POST request for registration
    @PostMapping("/register")
    public String registerNewAdmin(@ModelAttribute Admin newAdmin, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Save the new admin to the database
            adminService.createAdmin(newAdmin.getUsername(), newAdmin.getPassword());
            redirectAttributes.addFlashAttribute("success", "Admin registered successfully!");
            return "redirect:/admin/dashboard";  // Redirect after successful registration
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "admin/register";  // Return to registration page on failure
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // Invalidate the JWT token by clearing the cookie
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);  // Set the JWT token to null
        jwtCookie.setPath("/");  // Make sure it applies to all routes
        jwtCookie.setHttpOnly(true);  // Keep it HTTP-only
        jwtCookie.setMaxAge(0);  // Expire the cookie immediately
        response.addCookie(jwtCookie);

        // Redirect to the login page after logout
        redirectAttributes.addFlashAttribute("success", "You have been logged out!");
        return "redirect:/admin/login";  // You can customize the redirection path
    }

    // GET request for admin dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        return "admin/dashboard";  // Return the dashboard view
    }

    @GetMapping("/manage-admins")
    public String showAdminManagementPage(@RequestParam(value = "status", required = false) String status,  Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        CustomAdminDetails adminDetails = (CustomAdminDetails) authentication.getPrincipal();

        if (!adminDetails.canManageAdmins()) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to view this page");
            return "redirect:/admin/dashboard";  // Redirect if the admin is not authorized to manage other admins
        }

        // Generate a 6-digit random number as a string
        Random random = new Random();
        String randomPassword = String.format("%06d", random.nextInt(1000000));

        // Add randomPassword to the model
        model.addAttribute("randomPassword", randomPassword);

        List<Admin> adminList =  adminService.findAllByStatus(status);

        // Proceed with showing the admin management page
        model.addAttribute("admins", adminList);
        return "admin/manage-admin";
    }

    // Approve (Enable) admin
    @PostMapping("/manage-admins/{id}/approve")
    public String approveAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.updateAdminStatus(id, true);  // Set enabled to true
        adminService.getAdminById(id).ifPresent(admin -> redirectAttributes.addFlashAttribute("success", admin.getUsername()+ " has been Enabled!"));
        return "redirect:/admin/manage-admins";
    }

    // Disable admin
    @PostMapping("/manage-admins/{id}/disable")
    public String disableAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.updateAdminStatus(id, false);  // Set enabled to false
        adminService.getAdminById(id).ifPresent(admin -> redirectAttributes.addFlashAttribute("success", admin.getUsername()+ " has been disabled!"));
        return "redirect:/admin/manage-admins";
    }

    // Approve (Enable) admin
    @PostMapping("/manage-admins/{id}/power")
    public String powerManageAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.updateManageAdminStatus(id, true);  // Set enabled to true
        adminService.getAdminById(id).ifPresent(admin -> redirectAttributes.addFlashAttribute("success", admin.getUsername()+ " got the power to manage another admins"));
        return "redirect:/admin/manage-admins";
    }

    // Disable admin
    @PostMapping("/manage-admins/{id}/normal")
    public String normalManageAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.updateManageAdminStatus(id, false);  // Set enabled to false
        adminService.getAdminById(id).ifPresent(admin -> redirectAttributes.addFlashAttribute("success", admin.getUsername()+ " turns into normal admins"));
        return "redirect:/admin/manage-admins";
    }


    // Edit admin password (get form)
    @GetMapping("/manage-admins/{id}/password")
    public String editAdminPassword(@PathVariable Long id, Model model) {
        Admin admin = adminService.getAdminById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        // this method is not using for the edit password
        model.addAttribute("admin", admin);
        return "admin/edit-password";  // Thymeleaf template for password edit
    }

    // Save new password
    @PostMapping("/manage-admins/{id}/password")
    public String saveAdminPassword(@PathVariable Long id, @RequestParam String password, RedirectAttributes redirectAttributes) {
        //this method is used in the Edit Password Modal from the admin/manage-admin template
        adminService.updateAdminPassword(id, password);
        adminService.getAdminById(id).ifPresent(admin -> redirectAttributes.addFlashAttribute("success", admin.getUsername()+ "' password has been updated!"));
        return "redirect:/admin/manage-admins";
    }

    @PostMapping("/add")
    public String addAdmin(Admin admin, Authentication authentication) {
        CustomAdminDetails adminDetails = (CustomAdminDetails) authentication.getPrincipal();

        if (!adminDetails.canManageAdmins()) {
            return "redirect:/admin/dashboard";  // Redirect if the admin is not authorized to manage other admins
        }

        adminService.createAdmin(admin.getUsername(), admin.getPassword());
        return "redirect:/admin/manage-admins";
    }
}



