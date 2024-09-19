package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "verification_token",
        indexes = {
                @Index(
                        name = "idx_expiry_isverified_issubmitted",
                        columnList = "expiry_date, is_verified, is_submitted"
                )
        }
)
@Data
public class VerificationToken extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // The email to which the token is associated

    @Column(nullable = false, unique = true)
    private String token; // The unique verification token

    @Column(nullable = false)
    private LocalDateTime expiryDate; // Expiration date for the token (24 hours validity)

    private boolean isVerified;

    private boolean isSubmitted;

    // Method to check if the token is still valid
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryDate);
    }

    // Method to set the expiry date 24 hours from now
    public void setExpiryDate() {
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }
}