package com.salesapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/v1/auth/login", "/v1/auth/introspect", "/v1/auth/register", "/v1/auth/logout", "/v1/auth/refresh",
        "/v1/products", "/v1/products/*", "/v1/products/**",
        "/v1/categories", "/v1/categories/*", "/v1/categories/**",
        "/v1/users", "/v1/users/*", "/v1/users/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        String requestPath = request.getServletPath();
        System.out.println("JwtAuthenticationEntryPoint triggered for: " + requestPath);
        
        // Check if this is a public endpoint
        boolean isPublicEndpoint = PUBLIC_ENDPOINTS.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
            
        if (isPublicEndpoint) {
            System.out.println("Public endpoint accessed without authentication: " + requestPath);
            // For public endpoints, this should NOT happen if security is configured correctly
            // But if it does, we still return 401 to indicate the issue
        }

        // Return 401 with proper message for authentication failures
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseObject responseObject = ResponseObject.builder()
                .status(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseObject));
        response.flushBuffer();
        
        System.out.println("401 response sent with message: " + errorCode.getMessage());
    }
}
