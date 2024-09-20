package com.goonok.equalbangla.config;


import com.goonok.equalbangla.repository.AuditLogRepository;
import com.goonok.equalbangla.util.AuditListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityListenerConfiguration {

    @Autowired
    public void configure(AuditLogRepository auditLogRepository) {
        AuditListener.setAuditLogRepository(auditLogRepository);
    }
}
