package com.goonok.equalbangla.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goonok.equalbangla.model.AuditLog;
import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.repository.AuditLogRepository;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
public class AuditListener {

    private static AuditLogRepository auditLogRepository;

    // Static method to set the repository (from Spring context)
    public static void setAuditLogRepository(AuditLogRepository auditLogRepo) {
        auditLogRepository = auditLogRepo;
    }

    @PreUpdate
    public void logUpdate(Object entity) {
        if (entity instanceof Victim) {
            Victim victim = (Victim) entity;
            Long entityId = victim.getId();
            String modifiedBy = SecurityContextHolder.getContext().getAuthentication().getName();
            LocalDateTime now = LocalDateTime.now();

            String oldValue = getOldValue(entityId);  // Retrieve the old state
            String newValue = getNewValue(victim);    // Get the new state

            AuditLog auditLog = new AuditLog(
                    victim.getClass().getSimpleName(),
                    entityId,
                    "UPDATED",
                    modifiedBy,
                    now,
                    oldValue,
                    newValue
            );
            auditLogRepository.save(auditLog);
        }
    }

    // Similar logic for @PreRemove, etc.
    @PreRemove
    public void logDelete(Object entity) {
        if (entity instanceof Victim) {
            Victim victim = (Victim) entity;
            Long entityId = victim.getId();
            String modifiedBy = SecurityContextHolder.getContext().getAuthentication().getName();
            LocalDateTime now = LocalDateTime.now();

            // Capture the current state before deletion
            String oldValue = getNewValue(victim);  // Get the current state of the entity

            // Create and save the audit log
            AuditLog auditLog = new AuditLog(
                    victim.getClass().getSimpleName(),
                    entityId,
                    "DELETED",
                    modifiedBy,
                    now,
                    oldValue,
                    null // No new value as it’s being deleted
            );
            auditLogRepository.save(auditLog);
        }
    }

    private String getOldValue(Long entityId) {
        // Logic to retrieve the old value from the database (before the update)
        // This can be achieved by querying the database for the current state before changes
        // Convert the old state to a JSON string, if necessary
        return "{}";  // Dummy value, replace with real implementation
    }

    private String getNewValue(Victim victim) {
        // Convert the victim entity to JSON to capture its current state
        // You can use a library like Jackson to serialize the object to JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(victim);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting entity to JSON", e);
        }
    }
}
