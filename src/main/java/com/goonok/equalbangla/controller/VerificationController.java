package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.service.VerificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    // Show email input form for sending the verification code
    @GetMapping("/email")
    public String showEmailVerificationForm() {
        return "verify-email";  // Thymeleaf page for entering the email
    }

    // Process email input and send verification code
    @PostMapping("/send-code")
    public String sendVerificationCode(@RequestParam String email, Model model) {
        // Validate email domain (gmail, yahoo, hotmail)
        if (email.matches("^[\\w-\\.]+@(gmail|yahoo|hotmail)\\.com$")) {
            verificationService.generateVerificationToken(email);
            model.addAttribute("message", "Verification code sent to your email.");
        } else {
            model.addAttribute("error", "Invalid email domain. Please use Gmail, Yahoo, or Hotmail.");
        }
        return "verify-email";  // Return to the email form
    }

    // Process token verification
    @PostMapping("/verify")
    public String verifyToken(@RequestParam String token, Model model, HttpSession session) {
        boolean isValid = verificationService.verifyToken(token);
        if (isValid) {
            session.setAttribute("verifiedEmail", true);  // Store verification status in session
            model.addAttribute("message", "Email verified successfully!");
            return "form-selection";  // Allow the user to proceed to the form selection page
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "verify-email";  // Return to the verification page
        }
    }
}
