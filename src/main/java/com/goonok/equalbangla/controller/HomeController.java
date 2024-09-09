package com.goonok.equalbangla.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Serve the home page at the root URL
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Welcome to EqualBangla");
        return "home";  // Return the home.html Thymeleaf template
    }

    // Serve the about page
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("title", "About EqualBangla");
        return "about";  // Return the about.html Thymeleaf template
    }
}
