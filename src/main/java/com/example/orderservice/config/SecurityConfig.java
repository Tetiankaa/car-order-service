package com.example.orderservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Base64;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Value("${auth.issuers.google}")
    private String googleIssuer;
    @Value("${auth.issuers.microsoft}")
    private String microsoftIssuer;

    private Map<String, JwtDecoder> decoders;

    @PostConstruct
    public void setUpDecoders(){
        decoders = Map.of(
                googleIssuer, JwtDecoders.fromIssuerLocation(googleIssuer),
                microsoftIssuer,JwtDecoders.fromIssuerLocation(microsoftIssuer)
        );
    }

    @Bean
    public JwtDecoder multiTenancyJwtDecoder(){
        return token -> {
            String[] parts = token.split("\\.");

            if (parts.length <= 2){
                throw new JwtException("Invalid JWT.");
            }

            byte[] decodedToken = Base64.getDecoder().decode(parts[1]);

            String payload = new String(decodedToken);

            try {
                Map<String,Object> claims =objectMapper.readValue(payload, new TypeReference<>() {});

                String issuer = String.valueOf(claims.get("iss"));

                JwtDecoder jwtDecoder = decoders.get(issuer);

                if (jwtDecoder == null){
                    throw new JwtException("Unable to decode JWT. Issuer not found or not supported");
                }

                return jwtDecoder.decode(token);


            }catch (JsonProcessingException e){
                throw new JwtException(e.getMessage(), e );
            }

        };

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .authorizeHttpRequests(req->req
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(customizer->customizer.jwt(jwtConfigurer -> jwtConfigurer.decoder(multiTenancyJwtDecoder())))
                .build();
    }

}
