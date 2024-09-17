package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.repository.VictimRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private VictimRepository victimRepository;

    // Get statistics for each incident type (e.g., Injured, Missing, Death)
    public Map<String, Long> getIncidentTypeStatistics() {
        List<Object[]> results = victimRepository.countByIncidentType();
        // Convert the Object[] to a Map of incidentType -> count
        Map<String, Long> incidentTypeStats = new HashMap<>();
        for (Object[] result : results) {
            String incidentType = (String) result[0];
            Long count = (Long) result[1];
            incidentTypeStats.put(incidentType, count);
        }
        return incidentTypeStats;
    }

    // Get labels (months) for cases over time chart
    public List<String> getCasesOverTimeLabels() {
        List<Object[]> results = victimRepository.countCasesOverTime();
        List<String> labels = new ArrayList<>();
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            String monthName = Month.of(month).name(); // Convert month number to name
            labels.add(monthName);
        }
        return labels;
    }

    // Get data (counts) for cases over time chart
    public List<Long> getCasesOverTimeData() {
        List<Object[]> results = victimRepository.countCasesOverTime();
        List<Long> data = new ArrayList<>();
        for (Object[] result : results) {
            Long count = (Long) result[1];
            data.add(count);
        }
        return data;
    }

    // Get labels (distinct locations) for cases by location chart
    public List<String> getCasesByLocationLabels() {
        return victimRepository.getDistinctLocations();
    }

    // Get data (counts) for cases by location chart
    public List<Long> getCasesByLocationData() {
        List<Object[]> results = victimRepository.countCasesByLocation();
        List<Long> data = new ArrayList<>();
        for (Object[] result : results) {
            Long count = (Long) result[1];
            data.add(count);
        }
        return data;
    }

    // Get statistics for verification status (verified and unverified cases)
    public List<Long> getVerificationStatusStatistics() {
        Long verifiedCount = victimRepository.countVerified();
        Long unverifiedCount = victimRepository.countUnverified();
        Long rejectedCount = victimRepository.countRejectedByVerificationStatus();
        return List.of(verifiedCount, unverifiedCount, rejectedCount);
    }

    // Incident Type Chart - Pie Chart
    public String generateIncidentTypeChart(String filePath) throws IOException {
        // Dummy data, replace with real data retrieval
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Injured", 200);
        dataset.setValue("Missing", 150);
        dataset.setValue("Death", 50);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Incident Type Distribution", // Chart title
                dataset,                      // Data
                true,                         // Include legend
                true,
                false
        );

        // Customize the chart (optional)
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Injured", new Color(255, 255, 102));
        plot.setSectionPaint("Missing", new Color(102, 255, 102));
        plot.setSectionPaint("Death", new Color(255, 102, 102));
        plot.setBackgroundPaint(Color.white);

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), pieChart, 600, 400);

        return filePath;
    }

    // Cases Over Time - Bar Chart
    public String generateCasesOverTimeChart(String filePath) throws IOException {
        // Dummy data, replace with real data retrieval
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(50, "Cases", "January");
        dataset.addValue(60, "Cases", "February");
        dataset.addValue(70, "Cases", "March");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Cases Over Time",            // Chart title
                "Month",                      // X-axis label
                "Number of Cases",            // Y-axis label
                dataset,                      // Data
                PlotOrientation.VERTICAL,     // Orientation
                true,                         // Include legend
                true,
                false
        );

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), barChart, 600, 400);

        return filePath;
    }

    // Cases by Location - Bar Chart
    public String generateCasesByLocationChart(String filePath) throws IOException {
        // Dummy data, replace with real data retrieval
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100, "Cases", "Location A");
        dataset.addValue(150, "Cases", "Location B");
        dataset.addValue(200, "Cases", "Location C");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Cases by Location",           // Chart title
                "Location",                   // X-axis label
                "Number of Cases",            // Y-axis label
                dataset,                      // Data
                PlotOrientation.VERTICAL,     // Orientation
                true,                         // Include legend
                true,
                false
        );

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), barChart, 600, 400);

        return filePath;
    }

    // Verification Status - Pie Chart
    public String generateVerificationStatusChart(String filePath) throws IOException {
        // Dummy data, replace with real data retrieval
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Verified", 300);
        dataset.setValue("Unverified", 100);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Verification Status",         // Chart title
                dataset,                      // Data
                true,                         // Include legend
                true,
                false
        );

        // Customize the chart (optional)
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Verified", new Color(102, 255, 102));
        plot.setSectionPaint("Unverified", new Color(255, 102, 102));
        plot.setBackgroundPaint(Color.white);

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), pieChart, 600, 400);

        return filePath;
    }

    // Add more methods for different reports (time series, bar charts, etc.)

    public String generateCasesTimeSeriesChart(String filePath) throws IOException {
        // Dummy time series data - replace this with actual data
        TimeSeries series = new TimeSeries("Cases Over Time");

        series.add(new Day(1, 1, 2023), 100);  // January 1, 2023 - 100 cases
        series.add(new Day(15, 1, 2023), 150); // January 15, 2023 - 150 cases
        series.add(new Day(1, 2, 2023), 200);  // February 1, 2023 - 200 cases
        series.add(new Day(15, 2, 2023), 250); // February 15, 2023 - 250 cases

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
                "Cases Over Time",           // Title
                "Date",                      // X-axis Label
                "Number of Cases",           // Y-axis Label
                dataset,                     // Dataset
                true,                        // Legend
                true,
                false
        );

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), timeSeriesChart, 600, 400);

        return filePath;
    }


    public String generateStackedBarChart(String filePath) throws IOException {
        // Dummy dataset - replace with actual data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100, "Injured", "January");
        dataset.addValue(50, "Missing", "January");
        dataset.addValue(30, "Death", "January");

        dataset.addValue(120, "Injured", "February");
        dataset.addValue(60, "Missing", "February");
        dataset.addValue(40, "Death", "February");

        JFreeChart stackedBarChart = ChartFactory.createStackedBarChart(
                "Cases Breakdown by Month",  // Chart title
                "Month",                     // X-axis label
                "Number of Cases",           // Y-axis label
                dataset,                     // Dataset
                PlotOrientation.VERTICAL,    // Orientation
                true,                        // Include legend
                true,
                false
        );

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), stackedBarChart, 600, 400);

        return filePath;
    }


    public String generateHorizontalBarChart(String filePath) throws IOException {
        // Dummy dataset - replace with actual data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(150, "Cases", "District A");
        dataset.addValue(200, "Cases", "District B");
        dataset.addValue(180, "Cases", "District C");

        JFreeChart horizontalBarChart = ChartFactory.createBarChart(
                "Cases by District",          // Chart title
                "District",                   // X-axis label
                "Number of Cases",            // Y-axis label
                dataset,                      // Dataset
                PlotOrientation.HORIZONTAL,   // Horizontal Bar Chart
                true,                         // Include legend
                true,
                false
        );

        // Save as PNG
        ChartUtils.saveChartAsPNG(new File(filePath), horizontalBarChart, 600, 400);

        return filePath;
    }

    // CSV Report generation
    public void generateCsvReport(List<Victim> victims, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=victim_report.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Full Name,Incident Type,Incident Date,District,Police Station,Age,Gender,Occupation,Verification Status");

        for (Victim victim : victims) {
            writer.println(String.join(",",
                    String.valueOf(victim.getId()),
                    victim.getFullName(),
                    victim.getIncidentType(),
                    victim.getIncidentDate().toString(),
                    victim.getDistrict(),
                    victim.getPoliceStation(),
                    victim.getAge().toString(),
                    victim.getGender(),
                    victim.getOccupation(),
                    victim.getVerificationStatus()
            ));
        }

        writer.flush();
    }

    // Excel Report generation
    public void generateExcelReport(List<Victim> victims, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=victim_report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Victim Data");
        Row header = sheet.createRow(0);

        // Create headers
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Full Name");
        header.createCell(2).setCellValue("Incident Type");
        header.createCell(3).setCellValue("District");
        header.createCell(4).setCellValue("Incident Date");
        header.createCell(5).setCellValue("Verification Status");

        // Populate data
        int rowIndex = 1;
        for (Victim victim : victims) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(victim.getId());
            row.createCell(1).setCellValue(victim.getFullName());
            row.createCell(2).setCellValue(victim.getIncidentType());
            row.createCell(3).setCellValue(victim.getDistrict());
            row.createCell(4).setCellValue(victim.getIncidentDate().toString());
            row.createCell(5).setCellValue(victim.getVerificationStatus());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void generateExcelReportForEmail(List<Victim> victims, ByteArrayOutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create sheet and populate victim data
            Sheet sheet = workbook.createSheet("Victims");
            createHeaderRow(sheet);
            populateVictimData(sheet, victims);

            // Write to the output stream (in-memory)
            workbook.write(outputStream);
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Incident Type");
        headerRow.createCell(3).setCellValue("District");
        headerRow.createCell(4).setCellValue("Incident Date");
        headerRow.createCell(5).setCellValue("Verification Status");
    }

    private void populateVictimData(Sheet sheet, List<Victim> victims) {
        int rowNum = 1;
        for (Victim victim : victims) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(victim.getId());
            row.createCell(1).setCellValue(victim.getFullName());
            row.createCell(2).setCellValue(victim.getIncidentType());
            row.createCell(3).setCellValue(victim.getDistrict());
            row.createCell(4).setCellValue(victim.getIncidentDate().toString());
            row.createCell(5).setCellValue(victim.getVerificationStatus());
        }
    }


    // PDF Report generation
    public void generatePdfReport(List<Victim> victims, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=victim_report.pdf");

        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Load a default font (e.g., Arial or a custom font from resources)
            PDType0Font font = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/arial.ttf"));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Title
                contentStream.beginText();
                contentStream.setFont(font, 18);  // Set font size
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Victim Data Report");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(font, 12);  // Set font size for content
                contentStream.newLineAtOffset(100, 650);

                // Add Victim data
                for (Victim victim : victims) {
                    contentStream.showText("ID: " + victim.getId() + ", Name: " + victim.getFullName() +
                            ", Incident Type: " + victim.getIncidentType() +
                            ", District: " + victim.getDistrict() +
                            ", Incident Date: " + victim.getIncidentDate());
                    contentStream.newLineAtOffset(0, -15); // Move to next line
                }

                contentStream.endText();
            }

            // Save the PDF to the response output stream
            document.save(response.getOutputStream());
        }
    }

}
