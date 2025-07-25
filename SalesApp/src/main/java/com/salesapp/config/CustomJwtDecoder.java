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

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    @Lazy // Sử dụng @Lazy để break circular dependency
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        System.out.println("CustomJwtDecoder.decode() called with token: " + 
            (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null"));

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
