package com.salesapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(prePostEnabled = true)
@CrossOrigin(origins = "http://localhost:5173")
public class SecurityConfig {

    @Value("${jwt.signerKey}") //Đọc từ file application.yaml
    private String SIGNER_KEY;

    private final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/v*/auth/login", "/v*/auth/introspect", "/v*/auth/register", "/v*/auth/logout", "/v*/auth/refresh",
            "/*/users", "/*/users/*"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer ->
                                        jwtConfigurer.decoder(jwtDecoder())
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Ví dụ: Cho phép login
                        .anyRequest().permitAll()); // Các request khác cần xác thực



        return http.build();
    }

    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); //đã set pattern bên phía AuthenticationService

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    //giải mã JWT -> ĐÃ DÙNG CUSTOMJWTDECODER
    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //corsConfiguration.addAllowedOriginPattern("*"); // mở rộng cho tất cả các port localhost
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        corsConfiguration.addAllowedOrigin("http://192.168.1.81:8080");
        // hoặc cho phép tất cả local networks:
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
}
