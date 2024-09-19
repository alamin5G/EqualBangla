package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class InjuryDetails extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "victim_id")
    private Victim victim;

    @NotEmpty(message = "Describe the injury")
    private String natureOfInjury; // Description of the injury

    @NotEmpty(message = "Hospital name is required")
    private String hospitalName; // Name of the hospital or clinic where treated

    private String hospitalContact; // Contact details of the hospital

    @NotEmpty(message = "Select your current condition")
    private String currentCondition; // Status such as "hospitalized", "recovering", "disabled"

    @NotEmpty(message = "Select your severity type")
    private String severity; // Minor, moderate, severe

    private LocalDate dateOfTreatment; // Date the victim received treatment

    @NotEmpty(message = "Medical report path is required")
    private String medicalReportPath; // Path to the uploaded medical report

    @Transient
    @NotNull(message = "Medical Report is required")
    private MultipartFile medicalReportFile;

    @NotEmpty(message = "Injured Persons photograph path is required")
    private String injuredPersonPhotographPath; // Path to the uploaded medical report

    @Transient
    @NotNull(message = "Injured Persons photograph is required")
    private MultipartFile injuredPersonPhotographFile;


    @Column(name = "updated_by", updatable = false)
    private String updatedBy = "system";  // Store the username of the admin who updated this victim info
}
