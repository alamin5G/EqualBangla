package com.goonok.equalbangla.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private Long entityId;
    private String action;
    private String modifiedBy;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;

    public AuditLog(String entityName, Long entityId, String action, String modifiedBy,
                    LocalDateTime timestamp, String oldValue, String newValue) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.modifiedBy = modifiedBy;
        this.timestamp = timestamp;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    // Overloaded constructor for access logging
    public AuditLog(String modifiedBy, String entityName, String action, LocalDateTime timestamp) {
        this.entityName = entityName;
        this.action = action;
        this.modifiedBy = modifiedBy;
        this.timestamp = timestamp;
    }


}
