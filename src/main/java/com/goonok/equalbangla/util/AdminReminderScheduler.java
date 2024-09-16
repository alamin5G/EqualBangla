package com.goonok.equalbangla.util;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.service.EmailService;
import com.goonok.equalbangla.service.ReportService;
import com.goonok.equalbangla.service.VictimService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class AdminReminderScheduler {

    @Autowired
    private VictimService victimService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;  // Assuming you have an EmailService to send emails

    @Scheduled(cron = "0 0 8 * * TUE")  // Every Monday at 8 AM
    public void generateAndSendWeeklyReport() throws IOException {
        List<Victim> victims = victimService.getVictimsFromLastWeek(); // Fetch last week's victims
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Generate the report as Excel and store it in memory
        reportService.generateExcelReportForEmail(victims, outputStream);

        // Send the report via email
        try {
            emailService.sendEmailWithAttachment("alaminvai5g@gmail.com", "Weekly Victim Report",
                    "Please find the attached weekly victim report.", outputStream.toByteArray(), "victim_report.xlsx");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
