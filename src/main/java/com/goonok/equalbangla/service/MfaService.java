package com.goonok.equalbangla.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MfaService {

    private final Random random = new Random();
    private String generatedCode;

    // Generate MFA Code
    public String generateMfaCode() {
        generatedCode = String.format("%06d", random.nextInt(999999));
        return generatedCode;
    }

    // Send MFA Code (example, you can integrate email/SMS here)
    public void sendMfaCode(String contactInfo) {
        // Simulate sending the MFA code to an email/SMS
        System.out.println("MFA code sent to: " + contactInfo);
    }

    // Verify MFA Code
    public boolean verifyMfaCode(String enteredCode) {
        return generatedCode != null && generatedCode.equals(enteredCode);
    }
}
