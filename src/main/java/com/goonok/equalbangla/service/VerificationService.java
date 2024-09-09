package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.VerificationToken;
import com.goonok.equalbangla.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private EmailService emailService;

    // Generate a verification token and send it via email
    public String generateVerificationToken(String email) {
        // Generate a random token
        String token = UUID.randomUUID().toString();

        // Check if a token already exists for the email and remove it (to avoid multiple tokens)
        Optional<VerificationToken> existingToken = verificationTokenRepository.findByEmail(email);
        existingToken.ifPresent(verificationTokenRepository::delete);

        // Create a new VerificationToken
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setEmail(email);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate();  // Set expiry to 24 hours from now
        verificationTokenRepository.save(verificationToken);

        // Send the verification email with the token
        emailService.sendEmail(email, "Verification Code", "Your verification code is: " + token);

        return token;
    }

    // Verify if the token is valid
    public boolean verifyToken(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken.isPresent()) {
            VerificationToken tokenEntity = verificationToken.get();
            // Check if the token is still valid
            if (tokenEntity.isValid()) {
                // Token is valid; delete it to prevent reuse
                verificationTokenRepository.delete(tokenEntity);
                return true;
            }
        }
        return false;  // Invalid or expired token
    }
}
