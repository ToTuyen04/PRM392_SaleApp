package com.salesapp.config;

import com.salesapp.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    @Lazy
    private AuthenticationService authenticationService;

    private final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/v1/auth/login", "/v1/auth/introspect", "/v1/auth/register", "/v1/auth/logout", "/v1/auth/refresh",
            "/v1/products", "/v1/categories", "/v1/users"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        log.info("Processing request: {} {}", method, requestURI);

        // Check if this is a public endpoint
        boolean isPublicEndpoint = PUBLIC_ENDPOINTS.stream()
                .anyMatch(endpoint -> requestURI.startsWith(endpoint));

        if (isPublicEndpoint) {
            log.info("Public endpoint detected: {}, skipping JWT authentication", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // For protected endpoints, check JWT token
        final String token = getTokenFromRequest(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Validate token using AuthenticationService
                var introspectResult = authenticationService.introspect(
                        com.salesapp.dto.request.IntrospectRequest.builder()
                                .token(token)
                                .build()
                );

                if (introspectResult.isValid()) {
                    // Extract username from token and create authentication
                    String username = extractUsernameFromToken(token);
                    if (username != null) {
                        // Create a simple authentication object without requiring UserDetailsService
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                    username, 
                                    null, 
                                    Collections.singletonList(new SimpleGrantedAuthority("USER"))
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Successfully authenticated user: {}", username);
                    }
                }
            } catch (Exception e) {
                log.error("Cannot set user authentication: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractUsernameFromToken(String token) {
        try {
            // Parse JWT token to extract username/subject
            String[] chunks = token.split("\\.");
            if (chunks.length == 3) {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));
                // Simple JSON parsing to get subject
                if (payload.contains("\"sub\":")) {
                    String sub = payload.split("\"sub\":\"")[1].split("\"")[0];
                    return sub;
                }
            }
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
        }
        return null;
    }
}
