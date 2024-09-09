package com.goonok.equalbangla.config;

import com.goonok.equalbangla.security.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@Configuration
public class SecurityConfig implements ServletContextInitializer {

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Register rate limiting filter for specific endpoints
        FilterRegistration.Dynamic rateLimitFilter = servletContext.addFilter("rateLimitingFilter", new DelegatingFilterProxy("rateLimitingFilter"));
        rateLimitFilter.addMappingForUrlPatterns(null, false, "/verification/send-code");
        rateLimitFilter.addMappingForUrlPatterns(null, false, "/victims/submit-death-form", "/victims/submit-missing-form", "/victims/submit-injured-form");
    }
}
