package com.goonok.equalbangla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/static/images/**", "/about", "/verification/**", "/victims/**").permitAll()  // Public URLs
                        .anyRequest().authenticated()  // Other requests require authentication
                )
                // Disable form login and basic authentication using the new approach
                .formLogin(AbstractHttpConfigurer::disable)  // Disable form-based login
                .httpBasic(AbstractHttpConfigurer::disable);  // Disable HTTP Basic authentication

        // Disable CSRF protection for APIs or if not needed
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
