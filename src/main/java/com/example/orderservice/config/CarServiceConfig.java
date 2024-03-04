package com.example.orderservice.config;

import org.example.ApiClient;
import org.example.rest.CarsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class CarServiceConfig {

    @Bean
    public CarsApi carsApi(){
        return new CarsApi(authApiClient());
    }

    @Bean
    public ApiClient authApiClient(){
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8080");
        apiClient.setBearerToken(() -> {
            JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return authenticationToken.getToken().getTokenValue();
        });

        return apiClient;
    }
}
