package com.goonok.equalbangla.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class IPFilter implements Filter {

    private final Set<String> whitelistedIps = new HashSet<>();

    public IPFilter() {
        whitelistedIps.add("192.168.1.1"); // Add your allowed IPs here
        whitelistedIps.add("192.168.240.1"); // Add your allowed IPs here
        whitelistedIps.add("192.168.0.101"); // Add your allowed IPs here
        whitelistedIps.add("127.0.0.1");   // Localhost IP
        whitelistedIps.add("0:0:0:0:0:0:0:1"); // IPv6 Localhost
        whitelistedIps.add("103.192.156.122"); // IPv4 Localhost for me
        whitelistedIps.add("202.134.10.135"); // IPv4 Localhost for jeba
        whitelistedIps.add("59.152.3.20"); // IPv4 Localhost for sahed mubassir
        whitelistedIps.add("37.28.64.109"); // IPv4 Localhost for jahid er vai chatkhil (oman)
        whitelistedIps.add("172.16.65.46"); // IPv6 Localhost
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String ipAddress = httpRequest.getRemoteAddr();

        // Apply IP filtering only to /admin/** URLs
        if (requestURI.startsWith("/admin")) {
            if (!whitelistedIps.contains(ipAddress)) {
                response.getWriter().write("IP Address " + ipAddress + " is not allowed to access this resource.");
                throw new ServletException("IP Address " + ipAddress + " is not allowed to access this resource.");
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
