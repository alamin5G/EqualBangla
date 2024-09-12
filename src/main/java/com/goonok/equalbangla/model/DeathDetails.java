package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Transactional
public class DeathDetails extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "victim_id")
    private Victim victim;

    @NotEmpty(message = "Cause of death is required")
    private String causeOfDeath;

    @NotNull(message = "Date of death is required")
    private LocalDate dateOfDeath;

    private String timeOfDeath;

    @NotEmpty(message = "Burial/cremation details are required")
    private String burialCremationDetails;

    private String witnessAccount;

    @NotNull(message = "Autopsy information is required")
    private Boolean autopsyPerformed;

    // Store the file path in the database
    @NotNull(message = "Death Certificate path is required")
    private String deathCertificatePath;

    @NotNull(message = "Death photo path is required")
    private String deathPhotoPath;


    // Transient field for file upload
    @Transient
    @NotNull(message = "Upload .pdf format")
    private MultipartFile deathCertificateFile;

    //Transient field for file upload
    @Transient
    @NotNull(message = "upload in .jpg format")
    private MultipartFile deathPhotoFile;
}
