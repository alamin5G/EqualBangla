package com.goonok.equalbangla.controller;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.service.ReportService;
import com.goonok.equalbangla.service.VictimService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private VictimService victimService;

    @GetMapping()
    public String showReports(Model model, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String rootDir = request.getServletContext().getRealPath("/images/reports/");

        // Ensure the directory exists
        File dir = new File(rootDir);
        if (!dir.exists()) {
            dir.mkdirs(); // Create the directory if it doesn't exist
        }

        try {
            // Generate and save images
            String incidentTypeChart = reportService.generateIncidentTypeChart(rootDir + "incident_type_chart.png");
            String casesOverTimeChart = reportService.generateCasesOverTimeChart(rootDir + "cases_over_time_chart.png");
            String casesByLocationChart = reportService.generateCasesByLocationChart(rootDir + "cases_by_location_chart.png");
            String verificationStatusChart = reportService.generateVerificationStatusChart(rootDir + "verification_status_chart.png");

            // New charts
            String timeSeriesChart = reportService.generateCasesTimeSeriesChart(rootDir + "cases_time_series_chart.png");
            String stackedBarChart = reportService.generateStackedBarChart(rootDir + "stacked_bar_chart.png");
            String horizontalBarChart = reportService.generateHorizontalBarChart(rootDir + "horizontal_bar_chart.png");

            //additional chart

            // Add image paths to the model
            model.addAttribute("incidentTypeChart", "/images/reports/incident_type_chart.png");
            model.addAttribute("casesOverTimeChart", "/images/reports/cases_over_time_chart.png");
            model.addAttribute("casesByLocationChart", "/images/reports/cases_by_location_chart.png");
            model.addAttribute("verificationStatusChart", "/images/reports/verification_status_chart.png");

            // Add new charts
            model.addAttribute("timeSeriesChart", "/images/reports/cases_time_series_chart.png");
            model.addAttribute("stackedBarChart", "/images/reports/stacked_bar_chart.png");
            model.addAttribute("horizontalBarChart", "/images/reports/horizontal_bar_chart.png");


        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to generate report charts.");
        }


        return "admin/reports/report"; // Points to the Thymeleaf template
    }


    @GetMapping("/export/csv")
    public void exportVictimDataCsv(HttpServletResponse response) throws IOException {
        List<Victim> victims = victimService.getAllVictims(); // Fetch all victim data
        log.info("downloading the CSV file of victims {}" , victims.getFirst());
        log.info("downloading the CSV file of victims {}" , victims.getLast());
        reportService.generateCsvReport(victims, response);
    }

    @GetMapping("/export/excel")
    public void exportVictimDataExcel(HttpServletResponse response) throws IOException {
        List<Victim> victims = victimService.getAllVictims(); // Fetch all victim data
        log.info("downloading the Excel file of victims {}" , victims.getFirst());
        log.info("downloading the Excel file of victims {}" , victims.getLast());
        reportService.generateExcelReport(victims, response);
    }

    @GetMapping("/export/pdf")
    public void exportVictimDataPdf(HttpServletResponse response) throws IOException {
        List<Victim> victims = victimService.getAllVictims(); // Fetch all victim data
        log.info("downloading the PDF file of victims {}" , victims.getFirst());
        log.info("downloading the PDF file of victims {}" , victims.getLast());
        reportService.generatePdfReport(victims, response);
    }

    @GetMapping("/download-pdf")
    public void downloadVictimReport(HttpServletResponse response) throws IOException {
        List<Victim> victims = victimService.getVictimsFromLastWeek();
        reportService.generatePdfReport(victims, response);
    }


}
