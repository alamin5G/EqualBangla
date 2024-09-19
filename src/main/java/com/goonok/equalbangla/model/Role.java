package com.goonok.equalbangla.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Role extends LogEntity{ // still no use

    @Id
    private Long id;

    private String role;

    // getters and setters
}
