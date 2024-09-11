package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private String natureOfInjury; // Description of the injury

    private String hospitalName; // Name of the hospital or clinic where treated

    private String hospitalContact; // Contact details of the hospital

    private String currentCondition; // Status such as "hospitalized", "recovering", "disabled"

    private String severity; // Minor, moderate, severe

    private LocalDate dateOfTreatment; // Date the victim received treatment

    private String medicalReportPath; // Path to the uploaded medical report
}
