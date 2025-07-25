package com.salesapp.config;

import com.nimbusds.jose.JOSEException;
import com.salesapp.dto.request.IntrospectRequest;
import com.salesapp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;
import java.util.List;
import java.util.Arrays;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    @Lazy // Sử dụng @Lazy để break circular dependency
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;
    
    // Public endpoints that should skip JWT validation
    private final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/v1/auth/login", "/v1/auth/introspect", "/v1/auth/register", "/v1/auth/logout", "/v1/auth/refresh",
        "/v1/products", "/v1/products/", "/v1/products/stats/most-ordered",
        "/v1/categories", "/v1/categories/",
        "/v1/users", "/v1/users/"
    );

    @Override
    public Jwt decode(String token) throws JwtException {
        System.out.println("CustomJwtDecoder.decode() called with token: " + 
            (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null"));

        // Check if current request is to a public endpoint
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String requestPath = request.getRequestURI();
                System.out.println("Request path: " + requestPath);
                
                // Check if this is a public endpoint
                for (String publicEndpoint : PUBLIC_ENDPOINTS) {
                    if (requestPath.startsWith(publicEndpoint)) {
                        System.out.println("Public endpoint detected, skipping JWT validation for: " + requestPath);
                        // For public endpoints, create a minimal JWT without validation
                        if(Objects.isNull(nimbusJwtDecoder)) {
                            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
                            nimbusJwtDecoder = NimbusJwtDecoder
                                    .withSecretKey(secretKeySpec)
                                    .macAlgorithm(MacAlgorithm.HS512)
                                    .build();
                        }
                        return nimbusJwtDecoder.decode(token);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking request context: " + e.getMessage());
        }

        try {
            var response = authenticationService.introspect(IntrospectRequest.builder()
                    .token(token)
                    .build());
                    
            System.out.println("Token introspection result: " + response.isValid());
            
            if(!response.isValid()) {
                System.out.println("Token is invalid, throwing JwtException");
                throw new JwtException("Invalid token");
            }
        } catch (JOSEException | ParseException e) {
            System.out.println("Error during token introspection: " + e.getMessage());
            throw new JwtException(e.getMessage());
        }

        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        
        System.out.println("Token validation successful, decoding with NimbusJwtDecoder");
        return nimbusJwtDecoder.decode(token);
    }
}
