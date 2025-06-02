package com.xopix.productservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API-only service
                .authorizeHttpRequests(authorize -> authorize
                        // Publicly accessible endpoints
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/{productId}").permitAll()
                        // Admin-only endpoints (assuming JWT validation at Kong passes roles/authorities)
                        .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/products/{productId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/{productId}").permitAll()
                        .anyRequest().authenticated() // All other requests require authentication
                );
        // Note: For authentication, this service assumes JWTs are validated by Kong API Gateway
        // and user roles/authorities are passed in a way Spring Security can pick them up (e.g., custom filter, or JWT decoder).
        // For Phase 1, the `hasRole("ADMIN")` would rely on a very basic setup or be part of a later integration step.
        return http.build();
    }
}
