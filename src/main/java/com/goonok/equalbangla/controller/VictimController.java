package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.*;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.service.InjuryDetailsService;
import com.goonok.equalbangla.service.VerificationService;
import com.goonok.equalbangla.service.VictimService;
import com.goonok.equalbangla.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

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
    @Autowired
    private VerificationService verificationService;

    // Ensure the user is verified before accessing the form
    private boolean isVerified(HttpSession session) {
        return session.getAttribute("verifiedEmail") != null && (boolean) session.getAttribute("verifiedEmail");
    }

    // Show form selection after verification
    @GetMapping("/form-selection")
    public String showFormSelection(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isVerified(session)) {
            redirectAttributes.addFlashAttribute("message", "Your request is not verified! Please, verify your (another) email for a new submission");
            return "redirect:/verification/email";
        }
        return "form-selection";  // A Thymeleaf page with links to specific forms (Death, Missing, Injured)
    }

    // ===================== Death Form Handlers =====================

    // Show Death Form
    @GetMapping("/form-death")
    public String showDeathForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isVerified(session)) {
            redirectAttributes.addFlashAttribute("message", "Your request is not verified! Please, verify your (another) email for a new submission");
            return "redirect:/verification/email";
        }
        Victim victim = new Victim();
        victim.setIncidentType("Death");
        victim.setDeathDetails(new DeathDetails());
        model.addAttribute("victim", victim);
        return "form-death";  // Thymeleaf form page for submitting a death case
    }

    // Submit Death Form
    @PostMapping("/submit-death-form")
    public String submitDeathForm(@Valid @ModelAttribute Victim victim,
                                  BindingResult victimBindingResult,
                                  Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isVerified(session)) {
            redirectAttributes.addFlashAttribute("message", "Your request is not verified! Please, verify your (another) email for a new submission");
            return "redirect:/verification/email";
        }

        // Check if there are any validation errors for the Victim or DeathDetails
        if (victimBindingResult.hasErrors()) {
            model.addAttribute("victim", victim);
            return "form-death";  // Return to the death form with errors
        }

        // If there are no errors, save the death case
        victimService.saveDeathCase(victim, victim.getDeathDetails());
        model.addAttribute("message", "Death case submitted successfully!");

        //update the verification token saving status
        //if someone don't submitted a case successfully I am going to flag that as isSubmitted = false;
        //so that I can delete the false filed after a while automatically
        if (isVerified(session)) {
            String token = session.getAttribute("verifiedToken").toString();
            verificationService.updateVerificationToken(token);
        }
        session.invalidate();
        return "confirmation";  // Return confirmation page after submission
    }

    // ===================== Missing Form Handlers =====================

    // Show Missing Form
    @GetMapping("/form-missing")
    public String showMissingForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isVerified(session)) {
            redirectAttributes.addFlashAttribute("message", "Your request is not verified! Please, verify your (another) email for a new submission");
            return "redirect:/verification/email";  // Redirect if the user is not verified
        }
        Victim victim = new Victim();
        victim.setIncidentType("Missing");  // Set the default incident type
        victim.setMissingDetails(new MissingDetails());
        model.addAttribute("victim",victim);
        return "form-missing";  // Thymeleaf form page for submitting a missing case
    }

    // Submit Missing Form
    @PostMapping("/submit-missing-form")
    public String submitMissingForm(@Valid @ModelAttribute Victim victim,
                                    BindingResult victimBindingResult,
                                    Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if there are any validation errors for the Victim or MissingDetails

        if (!isVerified(session)) {
            redirectAttributes.addFlashAttribute("message", "Your request is not verified! Please, verify your (another) email for a new submission");
            return "redirect:/verification/email";  // Redirect if the user is not verified
        }

        victim.setIncidentType("Missing");  // Set the default incident type

        if (victimBindingResult.hasErrors()) {
            model.addAttribute("victim", victim);
            log.info("there is binding result error on the missing form..");
            return "form-missing";  // Return to the missing form with errors
        }

        // If there are no errors, save the missing case
        victimService.saveMissingCase(victim, victim.getMissingDetails());
        model.addAttribute("message", "Missing case submitted successfully!");

        //update the verification token saving status
        //if someone don't submitted a case successfully I am going to flag that as isSubmitted = false;
        //so that I can delete the false filed after a while automatically
        if (isVerified(session)) {
            String token = session.getAttribute("verifiedToken").toString();
            verificationService.updateVerificationToken(token);
        }

        session.invalidate();
        return "confirmation";  // Return confirmation page after submission
    }

    // ===================== Injured Form Handlers =====================

    // Show Injured Form
    @GetMapping("/form-injured")
    public String showInjuredForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isVerified(session)) {
            redirectAttributes.addFlashAttribute("message", "Your request is not verified! Please, verify your (another) email for a new submission");
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
                                    Model model, HttpSession session) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("victim", victim);
            log.info(bindingResult.getAllErrors().toString());
            return "form-injured";  // Return the form with error messages
        }


        // Save victim (which will also save injuryDetails due to cascading)
        victimService.saveInjuredCase(victim, victim.getInjuryDetails());
        model.addAttribute("message", "Injured case submitted successfully!");

        //update the verification token saving status
        //if someone don't submitted a case successfully I am going to flag that as isSubmitted = false;
        //so that I can delete the false filed after a while automatically
        if (isVerified(session)) {
            String token = session.getAttribute("verifiedToken").toString();
            verificationService.updateVerificationToken(token);
        }

        session.invalidate();
        return "confirmation";  // Return confirmation page after successful submission
    }

}