package com.example.s2581051.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class endpoint {

    @Bean
    public String ilpEndpoint() {
        String endpoint = System.getenv("ILP_ENDPOINT");

        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net/";
        }

        return endpoint;
    }
}
