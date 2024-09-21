package com.goonok.equalbangla.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Serve the home page at the root URL
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("pageTitle", "Welcome to Equal Bangladesh");
        return "home";  // Return the home.html Thymeleaf template
    }

    // Serve the about page
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("pageTitle", "About - Equal Bangladesh");
        return "about";  // Return the about.html Thymeleaf template
    }

    // File upload instructions page
    @GetMapping("/file-upload-instruction")
    public String showFileUploadInstructionPage(Model model) {
        model.addAttribute("pageTitle", "File Upload Instruction");
        return "file-upload-instruction";
    }

    // How page
    @GetMapping("/how")
    public String showHowPage(Model model) {
        model.addAttribute("pageTitle", "How - Equal Bangladesh");
        return "how";
    }

    // Why page
    @GetMapping("/why")
    public String showWhyPage(Model model) {
        model.addAttribute("pageTitle", "Why - Equal Bangladesh");
        return "why";
    }

    // Who page
    @GetMapping("/who")
    public String showWhoPage(Model model) {
        model.addAttribute("pageTitle", "Who - Equal Bangladesh");
        return "who";
    }

    @GetMapping("/required-information")
    public String showRequiredInformationPage(Model model) {
        model.addAttribute("pageTitle", "Required Information - Equal Bangladesh");
        return "required-information";
    }
}
