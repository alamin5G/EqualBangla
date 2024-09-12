package com.goonok.equalbangla.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class MissingDetails extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "victim_id")
    private Victim victim;


    private String physicalDescription; // Height, complexion, unique identifiers

    @NotBlank(message = "Last seen location is required")
    private String lastSeenLocation; // Last known location

    private LocalDate dateLastContacted; // Date of last known contact

    private String witnessAccount; // Description of last sighting or any witness accounts

    private String relationshipWithLastContact; // Relationship with the last known contact (friend, colleague)

    private Boolean authoritiesNotified; // Were the police or any authorities notified?

    @NotNull(message = "Death photo path is required")
    private String missingPhotographPath; // Path to the uploaded photograph

    private String policeReportPath; // Path to the uploaded photograph

    // Transient field for file upload
    @Transient
    @NotNull(message = "Upload missing person photo")
    private MultipartFile missingPhotographFile;

    @Transient
    private MultipartFile policeReportFile;
}
