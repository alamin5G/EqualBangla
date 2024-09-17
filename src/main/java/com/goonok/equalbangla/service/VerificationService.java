package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.VerificationToken;
import com.goonok.equalbangla.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private EmailService emailService;

    public void save(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

    public void setTokenExpiryDate(String token, LocalDateTime date) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken.isPresent()) {
            verificationToken.get().setVerified(true);
            verificationToken.get().setExpiryDate(date);
            verificationTokenRepository.save(verificationToken.get());
        }else {
            log.info("Token not found in the Verification Service");
        }
    }

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
        verificationToken.setVerified(false);
        verificationToken.setSubmitted(false);
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

    public void updateVerificationToken(String token){
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken.isPresent()) {
            verificationToken.get().setSubmitted(true);
            log.info("token is submitted as true");
            verificationTokenRepository.save(verificationToken.get());
        }
    }
}
