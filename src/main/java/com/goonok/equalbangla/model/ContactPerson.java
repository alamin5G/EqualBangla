package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class ContactPerson extends LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Contact person name is required")
    private String name;

    @NotBlank(message = "Relationship to the victim is required")
    private String relationshipToVictim; // Family, friend, acquaintance, etc.

    @Column(unique = true)
    @NotEmpty(message = "Phone number is required.")
    @Pattern(regexp = "^(\\+88)?01[3-9]{1}[0-9]{2}-?[0-9]{6}$", message = "Phone number is invalid")
    private String contactNumber;

    @NotBlank(message = "Address is required")
    private String address;

    private String email; // Email address for communication

    private String identificationDocument; // Identification document type or number
}
