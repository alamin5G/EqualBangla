package com.goonok.equalbangla.config;

import com.goonok.equalbangla.security.IPFilter;
import com.goonok.equalbangla.security.JwtTokenFilter;
import com.goonok.equalbangla.security.JwtTokenProvider;
import com.goonok.equalbangla.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private AdminService customUserDetailsService;

    @Autowired
    private IPFilter ipFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/how", "/why", "/who", "/required-information", "/file-upload-instruction", "/css/**", "/images/**", "/about", "/verification/**", "/victims/**").permitAll()
                        .requestMatchers("/admin/login").permitAll()  // Allow unauthenticated access to the login page
                        .requestMatchers("/admin/**", "/uploads/**").hasRole("ADMIN") // Only admin users can access /admin URLs
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // Add the IPFilter only for admin-related requests
                .addFilterBefore(ipFilter, BasicAuthenticationFilter.class)
                // Add JWT filter
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);  // Disable CSRF for stateless API

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCrypt for password hashing
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configure the authentication manager to use customUserDetailsService and passwordEncoder
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }
}
