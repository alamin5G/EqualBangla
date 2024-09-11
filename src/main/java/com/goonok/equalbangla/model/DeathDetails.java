package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Transactional
public class DeathDetails extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "victim_id", nullable = false)
    private Victim victim;

    private String causeOfDeath; // Known cause such as "police brutality", "gunfire", etc.

    private LocalDate dateOfDeath; // Date when the death occurred

    private String timeOfDeath; // Time of death (optional)

    private String burialCremationDetails; // Burial or cremation information

    private String witnessAccount; // Witnesses' description of what happened

    private Boolean autopsyPerformed; // Whether an autopsy was conducted

    private String deathCertificatePath; // Path to the uploaded death certificate
}
