package com.goonok.equalbangla.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Generate a SecretKey for signing the token
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Inject CustomUserDetailsService
    private final com.goonok.equalbangla.service.AdminService userDetailsService;

    public JwtTokenProvider(com.goonok.equalbangla.service.AdminService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Method to create a JWT token
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        // 1 hour validity
        long validityInMilliseconds = 3600000;
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)   // Set the claims
                .setIssuedAt(now)     // Set the issue date
                .setExpiration(validity) // Set the expiration date
                .signWith(secretKey, SignatureAlgorithm.HS256) // Sign with the secret key
                .compact();
    }

    // Method to validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to resolve the token from the request's Authorization header

    public String resolveToken(HttpServletRequest request) {
        // Check if the token is present in the Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Check if the token is present in cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();  // Return the token from the cookie
                }
            }
        }

        // If no token is found, return null
        return null;
    }

    // Extract authentication details from the token
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        // Use CustomUserDetailsService to fetch user details from the database
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Get the username from the token
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
