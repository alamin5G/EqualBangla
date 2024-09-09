package com.goonok.equalbangla.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Victim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotBlank(message = "Contact number is mandatory")
    private String contactNumber;

    @NotBlank(message = "National ID/Birth Certificate is required")
    private String nationalId;

    @NotBlank(message = "Incident type is required")
    private String incidentType;  // Injured, Missing, Death

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    @NotBlank(message = "Location of the incident is required")
    private String incidentLocation;

    @Lob
    private String incidentDescription; // Detailed description of the incident

    // Additional fields to capture more specific data
    private String email; // Optional, in case the victim wants to provide this
    private String occupation; // Optional field to store occupation of the victim
    private String participationRole; // Protester, supporter, bystander, etc.
    private String policeCaseReference; // Reference to any police report filed

    @OneToOne(mappedBy = "victim", cascade = CascadeType.ALL)
    private InjuryDetails injuryDetails;

    @OneToOne(mappedBy = "victim", cascade = CascadeType.ALL)
    private MissingDetails missingDetails;

    @OneToOne(mappedBy = "victim", cascade = CascadeType.ALL)
    private DeathDetails deathDetails;

    @ManyToOne
    @JoinColumn(name = "contact_person_id", nullable = false)
    private ContactPerson contactPerson;
}
