package com.goonok.equalbangla.controller;
import com.goonok.equalbangla.model.VerificationToken;
import com.goonok.equalbangla.service.VerificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;


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

    // Process email input and send verification code (OTP)
    @PostMapping("/send-code")
    public String sendVerificationCode(@RequestParam String email, Model model) {
        // Validate email domain (only allow Gmail, Yahoo, Hotmail)
        if (email.matches("^[\\w-\\.]+@(gmail|yahoo|hotmail)\\.com$")) {
            // Generate and send OTP
            boolean isExist = verificationService.generateVerificationToken(email);
            if (!isExist) {
                model.addAttribute("error", "This email already exists, you can't use this email again");
                return "verify-email";
            }
            model.addAttribute("email", email);
            return "otp-form";  // Redirect to OTP form after sending OTP
        } else {
            model.addAttribute("error", "Invalid email domain. Please use Gmail, Yahoo, or Hotmail.");
            return "verify-email";  // Return to the email form with error
        }
    }

    // Process token (OTP) verification
    @PostMapping("/verify")
    public String verifyToken(@RequestParam String email, @RequestParam String token, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String trimToken = token.trim();
        boolean isValid = verificationService.verifyOtp(email, trimToken);  // Verify OTP for the given email
        if (isValid) {

            // If OTP is valid, redirect to the victim form
            redirectAttributes.addFlashAttribute("message", "OTP verified successfully!");
            // If valid, store the verified email status in the session
            session.setAttribute("verifiedEmail", true);
           // session.setAttribute("verifiedEmailAddress", email);  // Store email in session for future use
           // session.setAttribute("tokenVerified", true);
            session.setAttribute("verifiedToken", trimToken);
            model.addAttribute("message", "Email verified successfully!");


            verificationService.setTokenExpiryDate(trimToken, LocalDateTime.now()); // change the token validity expire time

            // Redirect the user to the form selection page
            return "redirect:/victims/form-selection";  // Redirect to victim form
        } else {
            // If OTP is invalid or expired, show an error and return to the OTP form
            model.addAttribute("error", "Invalid or expired OTP.");
            redirectAttributes.addFlashAttribute("email", email);  // Keep the email in the form
            verificationService.deleteToken(email); //as email is not verified yet.
            redirectAttributes.addFlashAttribute("error", "Your OTP was wrong, please Enter a valid email");
            return "redirect:/verification/email";  // Show OTP form again
        }
    }
}