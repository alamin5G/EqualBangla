package com.goonok.equalbangla.util;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.repository.VictimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminReminderScheduler {

    @Autowired
    private VictimRepository victimRepository;

    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9 AM
    public void sendReminder() {
        List<Victim> unverifiedVictims = victimRepository.findByVerificationStatus("PENDING");

        if (!unverifiedVictims.isEmpty()) {
            // Logic to send email to admin (e.g., using JavaMailSender)
            System.out.println("Reminder: You have " + unverifiedVictims.size() + " pending verifications.");
        }
    }
}
