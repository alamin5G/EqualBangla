package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // The email to which the token is associated

    @Column(nullable = false, unique = true)
    private String token; // The unique verification token

    @Column(nullable = false)
    private LocalDateTime expiryDate; // Expiration date for the token (24 hours validity)

    // Method to check if the token is still valid
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryDate);
    }

    // Method to set the expiry date 24 hours from now
    public void setExpiryDate() {
        this.expiryDate = LocalDateTime.now().plusHours(24);
    }
}