package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.service.VictimDataStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/reports")
public class AdminDataStatisticsReportController {

    @Autowired
    private VictimDataStatisticsService victimDataStatisticsService;

    @GetMapping
    public String showReports(Model model) {

        // Incident Type Distribution
        Map<String, Long> incidentTypeStats = victimDataStatisticsService.getIncidentTypeStatistics();
        model.addAttribute("incidentTypeStats", incidentTypeStats);

        // Cases Over Time
        List<String> timeLabels = victimDataStatisticsService.getCasesOverTimeLabels();
        List<Long> timeData = victimDataStatisticsService.getCasesOverTimeData();
        model.addAttribute("timeLabels", timeLabels);
        model.addAttribute("timeData", timeData);

        // Cases by Location
        List<String> locationLabels = victimDataStatisticsService.getCasesByLocationLabels();
        List<Long> locationData = victimDataStatisticsService.getCasesByLocationData();
        model.addAttribute("locationLabels", locationLabels);
        model.addAttribute("locationData", locationData);

        // Verification Status
        List<Long> verificationStats = victimDataStatisticsService.getVerificationStatusStatistics();
        model.addAttribute("verificationStats", verificationStats);

        log.info("Test the model attributes: using print line");
        System.out.println(verificationStats);
        System.out.println(locationData);
        System.out.println(locationLabels);
        System.out.println(timeData);
        System.out.println(timeLabels);
        System.out.println(incidentTypeStats);

        return "admin/reports/report";
    }
}
