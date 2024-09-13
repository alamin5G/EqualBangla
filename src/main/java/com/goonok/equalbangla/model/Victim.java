package com.goonok.equalbangla.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
@Entity
@Transactional
@Data
public class Victim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Victim Fields...
    @NotBlank(message = "Full name is mandatory")
    private String fullName;

    @NotBlank(message = "Guardian's name is mandatory")
    private String guardianName;

    @NotBlank(message = "Gender is mandatory")
    private String gender;

    @NotNull(message = "Age is mandatory")
    private Integer age;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotBlank(message = "Police Station is required")
    private String policeStation;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Contact number is mandatory")
    @Pattern(regexp = "^(\\+88)?01[3-9]{1}[0-9]{2}-?[0-9]{6}$", message = "Phone number is invalid")
    private String contactNumber;

    @NotBlank(message = "National ID/Birth Certificate is required")
    private String nationalId;

    @NotBlank(message = "select what happened with you")
    private String incidentType;  // Injured, Missing, Death

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    @NotBlank(message = "Location of the incident is required")
    private String incidentLocation;

    @Lob
    @NotBlank(message = "Explain the incident")
    private String incidentDescription; // Detailed description of the incident

    // Optional Fields...
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "Occupation is required")
    private String occupation;
    @NotBlank(message = "What was the role during movement?")
    private String participationRole;
    //optional if have
    private String policeCaseReference;

    @NotEmpty(message = "Contact person name is required")
    private String contactName;

    @NotEmpty(message = "Relationship to the victim is required")
    private String relationshipToVictim;

    @NotEmpty(message = "Phone number is required.")
    @Pattern(regexp = "^(\\+88)?01[3-9]{1}[0-9]{2}-?[0-9]{6}$", message = "Phone number is invalid")
    private String contactPhone;

    @NotEmpty(message = "Address is required")
    private String contactAddress;

    private String contactEmail;  // Optional


    private String contactIdentificationDocument;  // Optional

    @OneToOne(cascade = CascadeType.ALL)  // Cascade save, update, delete, etc.
    @JoinColumn(name = "injury_details_id")
    private InjuryDetails injuryDetails;

    @OneToOne(mappedBy = "victim", cascade = CascadeType.ALL, orphanRemoval = true)
    private MissingDetails missingDetails;

    @OneToOne(mappedBy = "victim", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeathDetails deathDetails;

    @Column(name = "verification_status")
    private String verificationStatus; // Values: PENDING, VERIFIED, REJECTED

}