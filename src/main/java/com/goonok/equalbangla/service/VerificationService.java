package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.VerificationToken;
import com.goonok.equalbangla.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private EmailService emailService;

    // Generate a verification token and send it via email
    public boolean generateVerificationToken(String email) {


        // Check if a token already exists for the email and remove it (to avoid multiple tokens)
        Optional<VerificationToken> existingToken = verificationTokenRepository.findByEmail(email);
       // existingToken.ifPresent(verificationTokenRepository::delete);
        if (existingToken.isPresent()) {
            return false;
        }

        // Generate a random token
        int verificationCode = (int) (Math.random()*999999);
        String token = String.valueOf(verificationCode);

        // Create a new VerificationToken
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setEmail(email);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate();  // Set expiry to 24 hours from now
        verificationTokenRepository.save(verificationToken);

        // Send the verification email with the token
        emailService.sendEmail(email, "Equal Bangladesh - Verification Code", "Your verification code is: " + token +
                " It is valid for the next 24 hours.");

        return true;
    }

    // Verify if the token is valid
    // Verify the OTP
    public boolean verifyOtp(String email, String otp) {
        // Fetch the token for the given email from the database
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByEmail(email);

        if (tokenOptional.isPresent()) {
            VerificationToken token = tokenOptional.get();

            // Check if the token matches and is still valid (not expired)
            if (token.getToken().equals(otp) && token.isValid()) {
                // Token is valid and not expired
                return true;
            }
        }
        // OTP is invalid or expired
        return false;
    }

    public void deleteToken(String email) {
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByEmail(email);
        tokenOptional.ifPresent(verificationToken -> verificationTokenRepository.delete(verificationToken));
    }
}
