package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.*;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.service.InjuryDetailsService;
import com.goonok.equalbangla.service.VictimService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/victims")
public class VictimController {

    @Autowired
    private VictimService victimService;
    @Autowired
    private VictimRepository victimRepository;
    @Autowired
    private InjuryDetailsService injuryDetailsService;

    // Ensure the user is verified before accessing the form
    private boolean isVerified(HttpSession session) {
        return session.getAttribute("verifiedEmail") != null && (boolean) session.getAttribute("verifiedEmail");
    }

    // Show form selection after verification
    @GetMapping("/form-selection")
    public String showFormSelection() {
        return "form-selection";  // A Thymeleaf page with links to specific forms (Death, Missing, Injured)
    }

    // ===================== Death Form Handlers =====================

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
    public String submitDeathForm(@Valid @ModelAttribute Victim victim,
                                  BindingResult victimBindingResult,
                                  @Valid @ModelAttribute DeathDetails deathDetails,
                                  BindingResult deathBindingResult,
                                  Model model) {
        // Check if there are any validation errors for the Victim or DeathDetails
        if (victimBindingResult.hasErrors() || deathBindingResult.hasErrors()) {
            model.addAttribute("victim", victim);
            model.addAttribute("deathDetails", deathDetails);
            return "form-death";  // Return to the death form with errors
        }

        // If there are no errors, save the death case
        victimService.saveDeathCase(victim, deathDetails);
        model.addAttribute("message", "Death case submitted successfully!");
        return "confirmation";  // Return confirmation page after submission
    }

    // ===================== Missing Form Handlers =====================

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
    public String submitMissingForm(@Valid @ModelAttribute Victim victim,
                                    BindingResult victimBindingResult,
                                    @Valid @ModelAttribute MissingDetails missingDetails,
                                    BindingResult missingBindingResult,
                                    Model model) {
        // Check if there are any validation errors for the Victim or MissingDetails
        if (victimBindingResult.hasErrors() || missingBindingResult.hasErrors()) {
            model.addAttribute("victim", victim);
            model.addAttribute("missingDetails", missingDetails);
            return "form-missing";  // Return to the missing form with errors
        }

        // If there are no errors, save the missing case
        victimService.saveMissingCase(victim, missingDetails);
        model.addAttribute("message", "Missing case submitted successfully!");
        return "confirmation";  // Return confirmation page after submission
    }

    // ===================== Injured Form Handlers =====================

    // Show Injured Form
    @GetMapping("/form-injured")
    public String showInjuredForm(Model model, HttpSession session) {
        if (!isVerified(session)) {
            return "redirect:/verification/email";  // Redirect if the user is not verified
        }
        Victim victim = new Victim();
        victim.setIncidentType("Injured");  // Set the default incident type
        victim.setInjuryDetails(new InjuryDetails());
        model.addAttribute("victim", victim);
        return "form-injured";  // Thymeleaf form page for submitting an injured case
    }

    @PostMapping("/submit-injured-form")
    public String submitInjuredForm(@Valid @ModelAttribute Victim victim,
                                    BindingResult bindingResult,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("victim", victim);
            log.info(bindingResult.getAllErrors().toString());
            return "form-injured";  // Return the form with error messages
        }

        // Set the relationship between victim and injury details
        InjuryDetails injuryDetails = victim.getInjuryDetails();
        if (injuryDetails != null) {
            injuryDetails.setVictim(victim);  // Link the victim to injuryDetails
            log.info("Injury details is not null at the Victim Controller");
        }
        log.info("Injury details is null at the Victim Controller");

        // Save victim (which will also save injuryDetails due to cascading)
        victimRepository.save(victim);
        model.addAttribute("message", "Injured case submitted successfully!");
        return "confirmation";  // Return confirmation page after successful submission
    }

}