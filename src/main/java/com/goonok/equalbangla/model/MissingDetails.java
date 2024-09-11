package com.goonok.equalbangla.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private String lastSeenLocation; // Last known location

    private LocalDate dateLastContacted; // Date of last known contact

    private String witnessAccount; // Description of last sighting or any witness accounts

    private String relationshipWithLastContact; // Relationship with the last known contact (friend, colleague)

    private Boolean authoritiesNotified; // Were the police or any authorities notified?

    private String photographPath; // Path to the uploaded photograph
}
