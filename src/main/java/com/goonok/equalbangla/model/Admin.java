package com.goonok.equalbangla.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Admin extends LogEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password; // Store encrypted passwords

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;  // Store the username of the admin who created this user

    @Column(name = "updated_by", nullable = false, updatable = false)
    private String updatedBy;  // Store the username of the admin who created this user

    private boolean canManageAdmins = false; // Flag to indicate if this admin can manage other admins
    private boolean enabled = true; //is the admin can login or not - by default it is true;

}
