package com.goonok.equalbangla.service;


import com.goonok.equalbangla.model.AuditLog;
import com.goonok.equalbangla.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAccess(String username, String methodName, String status) {
        AuditLog log = new AuditLog(username, methodName, status, LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
