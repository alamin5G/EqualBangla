package com.goonok.equalbangla.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Entity
@Transactional
@Data
@Table(name = "victim", indexes = {
        @Index(name = "idx_incident_type", columnList = "incidentType"),
        @Index(name = "idx_incident_date", columnList = "incidentDate"),
        @Index(name = "idx_district", columnList = "district"),
        @Index(name = "idx_police_station", columnList = "policeStation"),
        @Index(name = "idx_age", columnList = "age"),
        @Index(name = "idx_gender", columnList = "gender"),
        @Index(name = "idx_occupation", columnList = "occupation")
})
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
    private String phoneNumber;

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
    @Size(min = 10, max = 500, message = "Description should be in between 10 to 500 character")
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

    @OneToOne(cascade = CascadeType.ALL)  // Cascade save, update, delete, etc.
    @JoinColumn(name = "missing_details_id")
    private MissingDetails missingDetails;

    @OneToOne(cascade = CascadeType.ALL)  // Cascade save, update, delete, etc.
    @JoinColumn(name = "death_details_id")
    private DeathDetails deathDetails;

    @Column(name = "verification_status")
    private String verificationStatus = "2"; // Values: PENDING, VERIFIED, REJECTED -0 for REJECTED, 1 for VERIFIED, 2 for PENDING

}