package com.goonok.equalbangla.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@EnableScheduling // Enable scheduling for clearing the map
public class RateLimitingFilter implements Filter {

    // In-memory store to track the requests per IP address and blacklisted IPs
    private final Map<String, RequestCounter> requestCountsPerIpAddress = new ConcurrentHashMap<>();
    private final Map<String, Long> blacklistedIps = new ConcurrentHashMap<>();

    // Define the rate limit parameters
    private static final long RATE_LIMIT_WINDOW_MS = 5 * 60 * 1000; // 5 minutes window
    private static final int MAX_REQUESTS_PER_WINDOW = 100;      // Max 100 requests per window
    private static final long BLACKLIST_DURATION_MS = 10 * 60 * 1000; // 10-minute ban

    // Maximum size for memory consumption threshold (in number of entries)
    private static final long MAX_MAP_ENTRIES = 10_000;  // Assuming 50MB for ~10,000 entries (estimate)

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check if the user has the ROLE_ADMIN role and bypass rate limiting if true
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && hasAdminRole(authentication)) {
            // Bypass rate limiting for admin users
            chain.doFilter(request, response);
            return;
        }


        String clientIpAddress = getClientIpAddress(httpRequest);

        // Check if the IP is blacklisted
        if (isBlacklisted(clientIpAddress)) {
            httpResponse.setStatus(429); // HTTP 429: Too Many Requests
            httpResponse.getWriter().write("Your IP has been blacklisted due to suspicious activity. Try again later.");
            return;
        }

        // Track request count and time window for the client IP
        RequestCounter requestCounter = requestCountsPerIpAddress.computeIfAbsent(clientIpAddress, k -> new RequestCounter());
        synchronized (requestCounter) {
            long currentTimeMillis = Instant.now().toEpochMilli();

            // Reset the counter if the time window has expired
            if (currentTimeMillis - requestCounter.getLastRequestTime() > RATE_LIMIT_WINDOW_MS) {
                requestCounter.setLastRequestTime(currentTimeMillis);
                requestCounter.setRequestCount(0); // Reset counter
            }

            // Check if the current request exceeds the allowed rate
            if (requestCounter.getRequestCount() < MAX_REQUESTS_PER_WINDOW) {
                requestCounter.incrementRequestCount();
                log.info("from the 100 request: you have used"  + requestCounter.getRequestCount() + " requests");
                chain.doFilter(request, response);  // Proceed with the request
            } else {
                // Too many requests; blacklist the IP
                blacklistIp(clientIpAddress);
                httpResponse.setStatus(429);  // HTTP 429: Too Many Requests
                httpResponse.getWriter().write("Too many requests. You have been temporarily blacklisted.");
                return;
            }
        }
    }


    // Helper method to check if the user has the ROLE_ADMIN role
    private boolean hasAdminRole(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            log.info("ROLE_ADMIN HAS BEEN FOUND on RATE LIMITING FILTER");
            return userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }


    // Method to check if an IP is blacklisted
    private boolean isBlacklisted(String ipAddress) {
        Long blacklistEndTime = blacklistedIps.get(ipAddress);
        if (blacklistEndTime != null) {
            // If current time is beyond the blacklist end time, remove from blacklist
            if (Instant.now().toEpochMilli() > blacklistEndTime) {
                blacklistedIps.remove(ipAddress);
                return false;
            }
            return true; // Still blacklisted
        }
        return false; // Not blacklisted
    }

    // Method to add an IP to the blacklist
    private void blacklistIp(String ipAddress) {
        long blacklistEndTime = Instant.now().toEpochMilli() + BLACKLIST_DURATION_MS;
        blacklistedIps.put(ipAddress, blacklistEndTime);
    }

    // Schedule clearing the in-memory map every 30 minutes OR when map size exceeds threshold
    @Scheduled(fixedRate = 30 * 60 * 1000) // Every 30 minutes
    public void clearRequestCounts() {
        // Check if the size of the map exceeds the threshold
        if (requestCountsPerIpAddress.size() > MAX_MAP_ENTRIES) {
            requestCountsPerIpAddress.clear();
            System.out.println("Cleared request counts due to memory usage exceeding limit at " + Instant.now());
        } else {
            // Clear based on time if size threshold not exceeded
            requestCountsPerIpAddress.clear();
            System.out.println("Cleared request counts after 30 minutes at " + Instant.now());
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }

    // Helper method to extract client IP address from the request
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // Inner class to track request count and last request time
    private static class RequestCounter {
        private int requestCount;
        private long lastRequestTime;

        public int getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        public long getLastRequestTime() {
            return lastRequestTime;
        }

        public void setLastRequestTime(long lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
        }

        public void incrementRequestCount() {
            this.requestCount++;
        }
    }
}
