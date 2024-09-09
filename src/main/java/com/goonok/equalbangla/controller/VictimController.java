package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.model.DeathDetails;
import com.goonok.equalbangla.model.MissingDetails;
import com.goonok.equalbangla.model.InjuryDetails;
import com.goonok.equalbangla.service.VictimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/victims")
public class VictimController {

    @Autowired
    private VictimService victimService;

    // Ensure the user is verified before accessing the form
    private boolean isVerified(HttpSession session) {
        return session.getAttribute("verifiedEmail") != null && (boolean) session.getAttribute("verifiedEmail");
    }

    // Show form selection after verification
    @GetMapping("/form-selection")
    public String showFormSelection() {
        return "form-selection";  // A Thymeleaf page with links to specific forms (Death, Missing, Injured)
    }

    // Show Death Form
    @GetMapping("/form-death")
    public String showDeathForm(Model model, HttpSession session) {
        if (!isVerified(session)) {
            return "redirect:/verification/email";  // Redirect if the user is not verified
        }
        model.addAttribute("victim", new Victim());
        model.addAttribute("deathDetails", new DeathDetails());
        return "form-death";  // Thymeleaf form page for submitting a death case
    }

    // Submit Death Form
    @PostMapping("/submit-death-form")
    public String submitDeathForm(@ModelAttribute Victim victim, @ModelAttribute DeathDetails deathDetails, Model model) {
        victimService.saveDeathCase(victim, deathDetails);  // Save death case using victimService
        model.addAttribute("message", "Death case submitted successfully!");
        return "confirmation";  // Confirmation page after form submission
    }

    // Show Missing Form
    @GetMapping("/form-missing")
    public String showMissingForm(Model model, HttpSession session) {
        if (!isVerified(session)) {
            return "redirect:/verification/email";  // Redirect if the user is not verified
        }
        model.addAttribute("victim", new Victim());
        model.addAttribute("missingDetails", new MissingDetails());
        return "form-missing";  // Thymeleaf form page for submitting a missing case
    }

    // Submit Missing Form
    @PostMapping("/submit-missing-form")
    public String submitMissingForm(@ModelAttribute Victim victim, @ModelAttribute MissingDetails missingDetails, Model model) {
        victimService.saveMissingCase(victim, missingDetails);  // Save missing case using victimService
        model.addAttribute("message", "Missing case submitted successfully!");
        return "confirmation";  // Confirmation page after form submission
    }

    // Show Injured Form
    @GetMapping("/form-injured")
    public String showInjuredForm(Model model, HttpSession session) {
        if (!isVerified(session)) {
            return "redirect:/verification/email";  // Redirect if the user is not verified
        }
        model.addAttribute("victim", new Victim());
        model.addAttribute("injuryDetails", new InjuryDetails());
        return "form-injured";  // Thymeleaf form page for submitting an injured case
    }

    // Submit Injured Form
    @PostMapping("/submit-injured-form")
    public String submitInjuredForm(@ModelAttribute Victim victim, @ModelAttribute InjuryDetails injuryDetails, Model model) {
        victimService.saveInjuredCase(victim, injuryDetails);  // Save injured case using victimService
        model.addAttribute("message", "Injured case submitted successfully!");
        return "confirmation";  // Confirmation page after form submission
    }

    // Other victim-related methods...
}
