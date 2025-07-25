package com.salesapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(prePostEnabled = true)
@CrossOrigin(origins = "http://localhost:5173")
public class SecurityConfig {

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/v*/auth/login", "/v*/auth/introspect", "/v*/auth/register", "/v*/auth/logout", "/v*/auth/refresh",
            "/v*/products", "/v*/products/*", "/v*/products/**",
            "/v*/categories", "/v*/categories/*", "/v*/categories/**",
            "/v*/users", "/v*/users/*", "/v*/users/**",
            "/*/users", "/*/users/*",
            // VNPay endpoints - VNPay callback không có JWT token
            "/v*/vnpay/payment-callback", "/v*/vnpay/payment-result",
            "/v*/vnpay/**",
            // AI Training endpoints - For development and testing
            "/v*/ai/train", "/v*/ai/test", "/v*/ai/api-docs", "/v*/ai/training/**",
            "/v*/ai-training/**",
            // Smart AI endpoints - AI with API calling capability
            "/v*/smart-ai/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Public endpoints không cần authentication
                        .anyRequest().authenticated()) // Các request khác cần xác thực
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        corsConfiguration.addAllowedOrigin("http://192.168.1.81:8080");
        corsConfiguration.addAllowedOriginPattern("http://192.168.*:*");
        corsConfiguration.addAllowedOriginPattern("http://10.*:*");
        corsConfiguration.addAllowedOriginPattern("http://localhost:*");

        UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(url);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public org.springframework.web.client.RestTemplate restTemplate() {
        return new org.springframework.web.client.RestTemplate();
    }
}
