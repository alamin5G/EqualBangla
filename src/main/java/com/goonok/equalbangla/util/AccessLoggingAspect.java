package com.goonok.equalbangla.util;

import com.goonok.equalbangla.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AccessLoggingAspect {

    @Autowired
    private AuditService auditService;

    @AfterReturning("execution(* com.goonok.equalbangla.service.VictimService.*(..))")
    public void logAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String adminName = SecurityContextHolder.getContext().getAuthentication().getName();

        auditService.logAccess(adminName, methodName, "SUCCESS");
    }
}

