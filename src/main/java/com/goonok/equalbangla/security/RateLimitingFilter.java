package com.goonok.equalbangla.security;

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

@Component
public class RateLimitingFilter implements Filter {

    // In-memory store to track the requests per IP address
    private final Map<String, RequestCounter> requestCountsPerIpAddress = new ConcurrentHashMap<>();

    // Define the rate limit parameters
    private static final long RATE_LIMIT_WINDOW_MS = 60 * 1000; // 1 minute window
    private static final int MAX_REQUESTS_PER_WINDOW = 10;       // Max 5 requests per window

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String clientIpAddress = getClientIpAddress(httpRequest);

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
                chain.doFilter(request, response);  // Proceed with the request
            } else {
                // Too many requests; reject the request
                httpResponse.setStatus(429);  // HTTP 429: Too Many Requests
                httpResponse.getWriter().write("Your (Rate) limit exceeded. Please try again later.");
                return;
            }
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